/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grapeshot.halfnes;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * 
 * @author Andrew
 */
public class NTSCRenderer extends Renderer {

	private int offset = 0;
	private int scanline = 0;
	//hm, if I downsampled these perfectly to 4Fsc i could get rid of matrix decode
	//and the sine tables altogether...
	private final static byte[][] colorphases = {
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },//0x00
			{ 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1 },//0x01
			{ 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0 },//0x02
			{ 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0 },//0x03
			{ 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0 },//0x04
			{ 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 },//0x05
			{ 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0 },//0x06
			{ 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 },//0x07
			{ 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1 },//0x08
			{ 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1 },//0x09
			{ 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1 },//0x0A
			{ 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 },//0x0B
			{ 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1 },//0x0C
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },//0x0D
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },//0x0E
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } };//0x0F
	private final static float[][][] lumas = genlumas();
	private final static int[][] coloremph = {
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1 },//X
			{ 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0 },//Y
			{ 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1 },//XY
			{ 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1 },//Z
			{ 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1 },//XZ
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1 },//YZ
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };//XYZ
	//private final static float sync = -0.359f;
	private int frames = 0;
	private final float[] i_filter = new float[12], q_filter = new float[12];
	final float[] sample = new float[2728];
	private final static int[] colortbl = genColorCorrectTbl();

	public NTSCRenderer() {
		int hue = -512;
		double col_adjust = 1.2 / .707;
		for (int j = 0; j < 12; ++j) {
			float angle = (float)(Math.PI * ((hue + (j << 8)) / (12 * 128.0) - 33.0 / 180));
			i_filter[j] = (float)(-col_adjust * Math.cos(angle));
			q_filter[j] = (float)(col_adjust * Math.sin(angle));
		}
		//        utils.printarray(i_filter);
		//        utils.printarray(q_filter);

	}

	public static int[] genColorCorrectTbl() {
		int[] corr = new int[256];
		//float gamma = 1.2;
		float brightness = 20;
		float contrast = 1;
		for (int i = 0; i < 256; ++i) {
			float br = (i * contrast - (128 * contrast) + 128 + brightness) / 255.f;
			corr[i] = clamp((int)(255 * Math.pow(br, 1.3)));
			//convert tv gamma image (~2.2-2.5) to computer gamma (~1.8)
		}
		return corr;
	}

	public static float[][][] genlumas() {
		float[][] lumas = {
				{ -0.117f, 0.000f, 0.308f, 0.715f },
				//0x00    0x10    0x20    0x30
				{ 0.397f, 0.681f, 1.0f, 1.0f }
		};
		float[][][] premultlumas = new float[lumas.length][lumas[0].length][2];
		for (int i = 0; i < lumas.length; ++i) {
			for (int j = 0; j < lumas[i].length; ++j) {
				premultlumas[i][j][0] = lumas[i][j];
				premultlumas[i][j][1] = lumas[i][j] * 0.735f;
			}
		}
		return premultlumas;
	}

	public final float[] ntsc_encode(int[] nescolors, int pxloffset, int bgcolor, boolean dotcrawl) {
		//part one of the process. creates a 2728 pxl array of floats representing
		//ntsc version of scanline passed to it. Meant to be called 240x a frame

		//todo:
		//-make this encode an entire frame at a time
		//-reduce # of array lookups (precalc. what is necessary)

		//first of all, increment scanline numbers and get the offset for this line.
		++scanline;
		if (scanline > 239) {
			scanline = 0;
			++frames;
			offset = ((frames & 1) == 0 && dotcrawl) ? 0 : 6;
		}
		offset = (offset + 4) % 12; //3 line dot crawl
		//offset = (offset + 6) % 12; //2 line dot crawl it couldve had
		int i, col, level, emphasis;
		//luminance portion of nes color is bits 4-6, chrominance part is bits 1-3
		//they are both used as the index into various tables
		//the chroma generator chops between 2 different voltages from luma table 
		//at a constant rate but shifted phase.

		//sync and front porch are not actually used by decoder so not implemented here
		//dot 0-200:sync
		//dot 200-232:black
		//dot 232-352:colorburst
		//dot 352-400:black       
		//dot 400-520 and 2568-2656: background color
		col = bgcolor & 0xf;
		level = (bgcolor >> 4) & 3;
		emphasis = (bgcolor >> 6);
		for (i = 400; i < 520; ++i) {
			final int phase = (i + offset) % 12;
			sample[i] = lumas[colorphases[col][phase]][level][coloremph[emphasis][phase]];
		}
		for (i = 2568; i < 2656; ++i) {
			final int phase = (i + offset) % 12;
			sample[i] = lumas[colorphases[col][phase]][level][coloremph[emphasis][phase]];
		}
		//dot 520-2568:picture
		for (i = 520; i < 2568; ++i) {
			if ((i & 7) == 0) {
				col = nescolors[(((i - 520) >> 3)) + pxloffset];
				if ((col & 0xf) > 0xd) {
					col = 0x0f;
				}
				level = (col >> 4) & 3;
				emphasis = (col >> 6);
				col &= 0xf;
			}
			final int phase = (i + offset) % 12;
			sample[i] = lumas[colorphases[col][phase]][level][coloremph[emphasis][phase]];
		}
		//dot 2656-2720:black
		return sample;
	}

	public final static float chroma_filterfreq = 3579000.f, pixel_rate = 42950000.f;
	private final static int coldelay = 12;
	final float[] chroma = new float[2728];
	final float[] luma = new float[2728];
	final float[] eye = new float[2728];
	final float[] queue = new float[2728];

	public final void ntsc_decode(final float[] ntsc, final int[] frame, int frameoff) {

		//decodes one scan line of ntsc video and outputs as rgb packed in int
		//uses the cheap TV method, which is filtering the chroma from the luma w/o
		//combing or buffering previous lines

		box_filter(ntsc, luma, chroma, 12);
		int cbst;
		//find color burst
		switch (offset) {
			case 10:
				cbst = 242;
				break;
			case 2:
				cbst = 250;
				break;
			case 6:
				cbst = 246;
				break;
			case 4:
				cbst = 248;
				break;
			case 8:
				cbst = 244;
				break;
			case 0:
			default:
				cbst = 240;
				break;
		}
		int x = 492;
		int j = 0;
		for (int i = (cbst - coldelay); i < 2620; ++i, ++j, ++cbst, j %= 12) {
			//matrix decode the color diff signals;
			eye[i] = i_filter[j] * chroma[cbst];
			queue[i] = q_filter[j] * chroma[cbst]; //comment out for teal and orange filter
		}

		lowpass_filter(eye, 0.06f);
		lowpass_filter(queue, 0.05f);
		for (int i = 0; i < frame_w; ++i, ++x) {
			frame[i + frameoff] = compose_col(
					((luma[x] <= 0) ? 0 : colortbl[clamp((int)(iqm[0][0] * luma[x] + iqm[0][1] * eye[x] + iqm[0][2] * queue[x]))]),
					((luma[x] <= 0) ? 0 : colortbl[clamp((int)(iqm[1][0] * luma[x] + iqm[1][1] * eye[x] + iqm[1][2] * queue[x]))]),
					((luma[x] <= 0) ? 0 : colortbl[clamp((int)(iqm[2][0] * luma[x] + iqm[2][1] * eye[x] + iqm[2][2] * queue[x]))]));
		}
	}

	private static int compose_col(int r, int g, int b) {
		return (r << 16) | (g << 8) | (b) | 0xff000000;
	}

	private final static int[][] iqm = { { 255, -244, 158 }, { 255, 69, -165 }, { 255, 282, 434 } };

	public static int clamp(final int a) {
		return (a != (a & 0xff)) ? ((a < 0) ? 0 : 255) : a;
	}

	public final static int frame_w = 704 * 3;
	int[] frame = new int[frame_w * 240];
	Kernel kernel = new Kernel(3, 3,
			new float[] { -.0625f, .125f, -.0625f,
					.125f, .75f, .125f,
					-.0625f, .125f, -.0625f });
	BufferedImageOp op = new ConvolveOp(kernel);

	@Override
	public BufferedImage render(int[] nespixels, int[] bgcolors, boolean dotcrawl) {
		for (int line = 0; line < 240; ++line) {
			ntsc_decode(ntsc_encode(nespixels, line * 256, bgcolors[line], dotcrawl), frame, line * frame_w);
		}
		BufferedImage i = getImageFromArray(frame, frame_w * 8, frame_w, 224);
		//i = op.filter(i, null); //sharpen
		return i;
	}

	public final void box_filter(final float[] in, final float[] lpout, final float[] hpout, final int order) {
		float accum = 0;
		for (int i = 358; i < 2656; ++i) {
			accum += in[i] - in[i - order];
			lpout[i] = accum / order;
			hpout[i] = in[i] - lpout[i];
		}
	}

	public final void lowpass_filter(final float[] arr, final float order) {
		float b = 0;
		for (int i = 358; i < 2656; ++i) {
			arr[i] -= b;
			b += arr[i] * order;
			arr[i] = b;
		}
	}
}
