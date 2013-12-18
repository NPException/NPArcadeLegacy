/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grapeshot.halfnes.audio;

import com.grapeshot.halfnes.utils;

/**
 *
 * @author Andrew
 */
public class VRCSoundChip implements ExpansionSoundChip {
    //to access sound test in Castlevania 3(J) - 
    //Hold down A+B while resetting, push Start twice

    private final Timer[] timers = {new SquareTimer(16), new SquareTimer(16)};
    private boolean[] enable = {true, true, true};
    private int[] volume = {0, 0, 0};
    private int sawdivider = 15;
    private int sawctr = 0;
    private int sawaccum = 0;
    private int sawseq = 0;
    private boolean clocknow = false;
    int currentval = 0;

    public final void write(final int register, final int data) {
        switch (register) {
            case 0x9000:
                volume[0] = data & 0xf;
                //duty cycle is between 12.5% and 50% unless last bit set and then it's forced to 100%
                timers[0].setduty(utils.getbit(data, 7) ? 16 : (((data >> 4) & 7) + 1));
                break;
            case 0x9001:
                timers[0].setperiod((timers[0].getperiod() & 0xf00) + data);
                break;
            case 0x9002:
                timers[0].setperiod((timers[0].getperiod() & 0xff) + ((data & 0xf) << 8));
                enable[0] = utils.getbit(data, 7);
                break;
            case 0xa000:
                volume[1] = data & 0xf;
                timers[1].setduty(utils.getbit(data, 7) ? 16 : (((data >> 4) & 7) + 1));
                break;
            case 0xa001:
                timers[1].setperiod((timers[1].getperiod() & 0xf00) + data);
                break;
            case 0xa002:
                timers[1].setperiod((timers[1].getperiod() & 0xff) + ((data & 0xf) << 8));
                enable[1] = utils.getbit(data, 7);
                break;
            case 0xb000:
                //saw accumulator
                sawaccum = data & 0x3f;
                break;
            case 0xb001:
                sawdivider &= 0xf00;
                sawdivider += data;
                break;
            case 0xb002:
                sawdivider &= 0xff;
                sawdivider += ((data & 0xf) << 8);
                enable[2] = utils.getbit(data, 7);
                break;
        }
    }

    public final void clock(final int cycle) {
        timers[0].clock(cycle);
        timers[1].clock(cycle);
        for (int i = 0; i < cycle; ++i) {
            clocksaw();
        }
    }

    public final int getval() {
        final int mixvol = 384 * (((enable[0] ? volume[0] : 0) * timers[0].getval()
                + (enable[1] ? volume[1] : 0) * timers[1].getval())
                + (enable[2] ? ((volume[2] & 0xff) >> 3) : 0));
        return mixvol;
    }

    private void clocksaw() {
        --sawctr;
        if (sawctr < 0) {
            sawctr = sawdivider;
            if (clocknow) {
                clocknow = false;
                volume[2] += sawaccum;
                ++sawseq;
                if (sawseq > 6) {
                    sawseq = 0;
                    volume[2] = 0;
                }
            } else {
                clocknow = true;
            }
        }
    }
}
