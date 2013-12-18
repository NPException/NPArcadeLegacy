package com.grapeshot.halfnes.mappers;
//HalfNES, Copyright Andrew Hoffman, October 2010

import com.grapeshot.halfnes.*;
import com.grapeshot.halfnes.audio.*;

public class VRC7Mapper extends Mapper {
    //need to add extra audio still.

    int prgbank0, prgbank1, prgbank2;
    int[] chrbank = {0, 0, 0, 0, 0, 0, 0, 0};
    boolean irqmode, irqenable, irqack, firedinterrupt = false;
    int irqreload, irqcounter = 22;
    int regaddr = 0;
    ExpansionSoundChip sndchip = new YMSoundChip();
    boolean hasInitSound = false;

    @Override
    public void loadrom() throws BadMapperException {
        super.loadrom();
        // needs to be in every mapper. Fill with initial cfg
        for (int i = 1; i <= 32; ++i) {
            //map last banks in to start off
            prg_map[32 - i] = (prgsize - (1024 * i)) % prgsize;
        }
        for (int i = 0; i < 8; ++i) {
            chr_map[i] = (1024 * i) & (chrsize - 1);
        }
    }

    @Override
    public final void cartWrite(final int addr, final int data) {
        if (addr < 0x8000 || addr > 0xffff) {
            super.cartWrite(addr, data);
            return;
        }
        if (irqmode && irqenable) {
            //if irq prescaler is disabled should cause a clock of counter every cycle
            //but i can't interrupt every cycle, so i'm settling for every ram read
            notifyscanline(999);
            notifyscanline(999);
        }

        final boolean bit0 = utils.getbit(addr, 4) | utils.getbit(addr, 3);
        final boolean bit1 = utils.getbit(addr, 5);
        switch (addr >> 12) {
            case 0x8:
                if (bit0) {
                    //8010,8008: prg bank 1 select
                    prgbank1 = data;
                } else {
                    //8000: prg bank 0 select
                    prgbank0 = data;
                }
                setbanks();
                break;
            case 0x9:
                if (!bit0 && !bit1) {
                    prgbank2 = data;
                    setbanks();
                } else if (bit0 && bit1) {
                    //$9030: data write to sndchip
                    sndchip.write(regaddr, data);
                } else {
                    //$9010: sndchip register select
                    regaddr = data;
                }
                break;
            case 0xa:
                //character bank selects
                chrbank[(bit0 ? 1 : 0)] = data;
                setbanks();
                break;
            case 0xb:
                //character bank selects
                chrbank[(bit0 ? 1 : 0) + 2] = data;
                setbanks();
                break;
            case 0xc:
                //character bank selects
                chrbank[(bit0 ? 1 : 0) + 4] = data;
                setbanks();
                break;
            case 0xd:
                //character bank selects
                chrbank[(bit0 ? 1 : 0) + 6] = data;
                setbanks();
                break;
            case 0xe:
                if (bit0) {
                    //irq latch
                    irqreload = data;
                } else {
                    //mirroring select
                    switch (data & 3) {
                        case 0:
                            setmirroring(Mapper.MirrorType.V_MIRROR);
                            break;
                        case 1:
                            setmirroring(Mapper.MirrorType.H_MIRROR);
                            break;
                        case 2:
                            setmirroring(Mapper.MirrorType.SS_MIRROR0);
                            break;
                        case 3:
                            setmirroring(Mapper.MirrorType.SS_MIRROR1);
                            break;
                    }
                }
                break;
            case 0xf:
                //irq control
                if (bit0) {
                    //irq ack
                    irqenable = irqack;
                    if (firedinterrupt) {
                        --cpu.interrupt;
                    }
                    firedinterrupt = false;
                } else {
                    //irq control
                    irqack = utils.getbit(data, 0);
                    irqenable = utils.getbit(data, 1);
                    irqmode = utils.getbit(data, 2);
                    if (irqenable) {
                        irqcounter = irqreload;
                    }
                    if (firedinterrupt) {
                        --cpu.interrupt;
                    }
                    firedinterrupt = false;
                }
        }
    }

    private void setbanks() {
        //map prg banks
        //last 8k fixed to end of rom
        for (int i = 1; i <= 8; ++i) {
            prg_map[32 - i] = prgsize - (1024 * i);
        }
        //first bank set to prg0 register
        for (int i = 0; i < 8; ++i) {
            prg_map[i] = (1024 * (i + 8 * prgbank0)) % prgsize;
        }
        //second bank set to prg1 register
        for (int i = 0; i < 8; ++i) {
            prg_map[i + 8] = (1024 * (i + 8 * prgbank1)) % prgsize;
        }
        //third bank set to prg2 register
        for (int i = 0; i < 8; ++i) {
            prg_map[i + 16] = (1024 * (i + 8 * prgbank2)) % prgsize;
        }

        //map chr banks
        for (int i = 0; i < 8; ++i) {
            setppubank(1, i, chrbank[i]);
        }
    }

    private void setppubank(final int banksize, final int bankpos, final int banknum) {
//        System.err.println(banksize + ", " + bankpos + ", "+ banknum);
        for (int i = 0; i < banksize; ++i) {
            chr_map[i + bankpos] = (1024 * ((banknum) + i)) % chrsize;
        }
//        utils.printarray(chr_map);
    }

    @Override
    public void notifyscanline(final int scanline) {
        if (!hasInitSound) {
            //tiny hack, because the APU is not initialized until AFTER this happens
            //TODO: this really should not need to be here.
            cpuram.apu.addExpnSound(sndchip);
            hasInitSound = true;
        }
        if (irqenable) {
            if (irqcounter == 255) {
                irqcounter = irqreload;
                //System.err.println("Interrupt @ Scanline " + scanline + " reload " + irqreload);
                if (!firedinterrupt) {
                    ++cpu.interrupt;
                }
                firedinterrupt = true;
            } else {
                ++irqcounter;
            }
        }
    }
}
