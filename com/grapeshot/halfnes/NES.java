package com.grapeshot.halfnes;

import okushama.arcade.system.programs.nes.ProgramNESEmulator;

import com.grapeshot.halfnes.mappers.BadMapperException;
import com.grapeshot.halfnes.mappers.Mapper;

/**
 * 
 * @author Andrew Hoffman
 */
public class NES implements Runnable {

	private Mapper mapper;
	public APU apu;
	public CPU cpu;
	public CPURAM cpuram;
	public PPU ppu;
	private ControllerInterface controller1, controller2;
	final public static String VERSION = "056";
	public boolean runEmulation = false;
	private boolean dontSleep = false;
	public long frameStartTime, framecount, frameDoneTime;
	private boolean frameLimiterOn = true;
	private String curRomPath, curRomName;
	public GUIInterface gui;
	private FrameLimiterInterface limiter = new FrameLimiterImpl(this);
	// Pro Action Replay device
	private ActionReplay actionReplay;

	public ProgramNESEmulator emu;

	public NES(ProgramNESEmulator e) {
		emu = e;
		gui = new GUIImpl(this, emu);
		gui.run();
	}

	public void run(final String romtoload) {
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 1);
		//set thread priority higher than the interface thread
		curRomPath = romtoload;
		loadROM(romtoload);
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (true) {
			if (runEmulation) {
				frameStartTime = System.nanoTime();
				actionReplay.applyPatches();
				runframe();
				if (frameLimiterOn && !dontSleep) {
					limiter.sleep();
				}
				frameDoneTime = System.nanoTime() - frameStartTime;
			}
			else {
				limiter.sleepFixed();
				if (ppu != null && framecount > 1) {
					render.run();
				}
			}
			try {
				//Thread.sleep(10);
			}
			catch (Exception e) {

			}
		}
	}

	Runnable render = new Runnable() {
		@Override
		public void run() {
			gui.render();
		}
	};

	public synchronized void runframe() {
		final int scanlinectrfire = 256;
		//the main method sequencing everything that has to happen in the nes each frame
		//loops unrolled a bit to avoid some conditionals every cycle
		//vblank
		//start by setting nmi
		if ((utils.getbit(ppu.ppuregs[0], 7))) {
			cpu.runcycle(241, 9000);
			cpu.nmi();
			// do the nmi but let cpu run ONE extra instruction first
			// still necessary for Vice - Project Doom
		}
		for (int scanline = 241; scanline < 261; ++scanline) {
			//most of vblank period
			cpu.cycle(scanline, scanlinectrfire);
			mapper.notifyscanline(scanline);
			cpu.cycle(scanline, 341);
		}
		//scanline 261 
		//turn off vblank flag
		cpu.cycle(261, 6);
		ppu.setvblankflag(false);
		cpu.cycle(261, 30);
		// turn off sprite 0, sprite overflow flags
		ppu.ppuregs[2] &= 0x9F;
		cpu.cycle(261, scanlinectrfire);
		mapper.notifyscanline(261);
		cpu.cycle(261, (((framecount & 1) == 1) && utils.getbit(ppu.ppuregs[1], 3)) ? 340 : 341);
		//odd frames are shorter by one PPU pixel if rendering is on.

		dontSleep = apu.bufferHasLessThan(1000);
		//if the audio buffer is completely drained, don't sleep for this frame
		//this is to prevent the emulator from getting stuck sleeping too much
		//on a slow system or when the audio buffer runs dry.

		apu.finishframe();
		cpu.modcycles();
		//active drawing time
		for (int scanline = 0; scanline < 240; ++scanline) {
			if (!ppu.drawLine(scanline)) { //returns true if sprite 0 hits
				cpu.cycle(scanline, scanlinectrfire);
				mapper.notifyscanline(scanline);
			}
			else {
				//it is de sprite zero line
				final int sprite0x = ppu.getspritehit();
				if (sprite0x < scanlinectrfire) {
					cpu.cycle(scanline, sprite0x);
					ppu.ppuregs[2] |= 0x40; //sprite 0 hit
					cpu.cycle(scanline, scanlinectrfire);
					mapper.notifyscanline(scanline);
				}
				else {
					cpu.cycle(scanline, scanlinectrfire);
					mapper.notifyscanline(scanline);
					cpu.cycle(scanline, sprite0x);
					ppu.ppuregs[2] |= 0x40; //sprite 0 hit
				}
			}
			//and finish out the scanline
			cpu.cycle(scanline, 341);
		}
		//scanline 240: dummy fetches
		cpu.cycle(240, scanlinectrfire);
		mapper.notifyscanline(240);
		cpu.cycle(240, 341);
		//set the vblank flag
		ppu.setvblankflag(true);
		//render the frame
		ppu.renderFrame(gui);
		if ((framecount & 2047) == 0) {
			//save sram every 30 seconds or so
			saveSRAM(true);
		}
		++framecount;
	}

	public void setControllers(ControllerInterface controller1, ControllerInterface controller2) {
		this.controller1 = controller1;
		this.controller2 = controller2;
	}

	public void toggleFrameLimiter() {
		if (frameLimiterOn) {
			frameLimiterOn = false;
		}
		else {
			frameLimiterOn = true;
		}
	}

	public synchronized void loadROM(final String filename) {
		runEmulation = false;
		if (FileUtils.exists(filename)
				&& (FileUtils.getExtension(filename).equalsIgnoreCase(".nes")
				|| FileUtils.getExtension(filename).equalsIgnoreCase(".nsf"))) {
			Mapper newmapper;
			try {
				final ROMLoader loader = new ROMLoader(filename);
				loader.parseHeader();
				newmapper = Mapper.getCorrectMapper(loader);
				newmapper.setLoader(loader);
				newmapper.loadrom();
			}
			catch (BadMapperException e) {
				gui.messageBox("Error Loading File: ROM is"
						+ " corrupted or uses an unsupported mapper.\n" + e.getMessage());
				return;
			}
			catch (Exception e) {
				gui.messageBox("Error Loading File: ROM is"
						+ " corrupted or uses an unsupported mapper.\n" + e.toString() + e.getMessage());
				e.printStackTrace();
				return;
			}
			if (apu != null) {
				//if rom already running save its sram before closing
				apu.destroy();
				saveSRAM(false);
				//also get rid of mapper etc.
				mapper.destroy();
				cpu = null;
				cpuram = null;
				ppu = null;
			}
			mapper = newmapper;
			//now some annoying getting of all the references where they belong
			cpuram = mapper.getCPURAM();
			actionReplay = new ActionReplay(cpuram);
			cpu = mapper.cpu;
			ppu = mapper.ppu;
			apu = new APU(this, cpu, cpuram);
			cpuram.setAPU(apu);
			cpuram.setPPU(ppu);
			curRomPath = filename;
			curRomName = FileUtils.getFilenamefromPath(filename);

			framecount = 0;
			//if savestate exists, load it
			if (mapper.hasSRAM()) {
				loadSRAM();
			}
			//and start emulation
			cpu.init();
			mapper.init();
			runEmulation = true;
		}
		else {
			gui.messageBox("Could not load file:\nFile " + filename + "\n"
					+ "does not exist or is not a valid NES game.");
		}
	}

	private void saveSRAM(final boolean async) {
		if (mapper != null && mapper.hasSRAM() && mapper.supportsSaves()) {
			if (async) {
				FileUtils.asyncwritetofile(mapper.getPRGRam(), FileUtils.stripExtension(curRomPath) + ".sav");
			}
			else {
				FileUtils.writetofile(mapper.getPRGRam(), FileUtils.stripExtension(curRomPath) + ".sav");
			}
		}
	}

	private void loadSRAM() {
		final String name = FileUtils.stripExtension(curRomPath) + ".sav";
		if (FileUtils.exists(name) && mapper.supportsSaves()) {
			mapper.setPRGRAM(FileUtils.readfromfile(name));
		}

	}

	public void quit() {
		//save SRAM and quit
		if (cpu != null && curRomPath != null) {
			cpu.reset();
			runEmulation = false;
			saveSRAM(false);
		}
		//System.exit(0);
	}

	public synchronized void reset() {
		if (cpu != null) {
			mapper.reset();
			cpu.reset();
			runEmulation = true;
			apu.pause();
			apu.resume();
		}
		//reset frame counter as well because PPU is reset
		//on Famicom, PPU is not reset when Reset is pressed
		//but some NES games expect it to be and you get garbage.
		framecount = 0;
	}

	public synchronized void reloadROM() {
		loadROM(curRomPath);
	}

	public synchronized void pause() {
		if (apu != null) {
			apu.pause();
		}
		runEmulation = false;
	}

	public long getFrameTime() {
		return frameDoneTime;
	}

	public String getrominfo() {
		if (mapper != null) {
			return mapper.getrominfo();
		}
		return null;
	}

	public synchronized void frameAdvance() {
		runEmulation = false;
		if (cpu != null) {
			runframe();
		}
	}

	public synchronized void resume() {
		if (apu != null) {
			apu.resume();
		}
		if (cpu != null) {
			runEmulation = true;
		}
	}

	public String getCurrentRomName() {
		return curRomName;
	}

	public boolean isFrameLimiterOn() {
		return frameLimiterOn;
	}

	public void messageBox(final String string) {
		gui.messageBox(string);
	}

	public ControllerInterface getcontroller1() {
		return controller1;
	}

	public ControllerInterface getcontroller2() {
		return controller2;
	}

	void setApuVol() {
		if (apu != null) {
			apu.setParameters();
		}
	}

	/**
	 * Access to the Pro Action Replay device.
	 * 
	 * @return
	 */
	public synchronized ActionReplay getActionReplay() {
		return actionReplay;
	}
}
