package page.info.edit;

import common.CommonStatic;
import common.battle.data.CustomUnit;
import common.pack.Identifier;
import common.pack.UserProfile;
import common.util.Data;
import common.util.lang.ProcLang;
import common.util.unit.AbUnit;
import org.jcodec.common.tools.MathUtil;
import page.*;
import utilpc.Interpret;
import utilpc.Theme;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

class PCoinEditTable extends Page {
    private static final long serialVersionUID = 1L;

    private static class talentData {

        private final String name;
        private final int key;

        private talentData(String text, int ID) {
            name = text;
            key = ID;
        }

        @Override
        public String toString() { return name; }
        private int getValue() { return key; }
    }

    private static class NPList extends JList<talentData> {
        private static final long serialVersionUID = 1L;

        protected NPList() {
            setSelectionBackground(Theme.DARK.NIMBUS_SELECT_BG);
        }
        protected void setListIcons() {
            setCellRenderer(new DefaultListCellRenderer() {

                private static final long serialVersionUID = 1L;

                @Override
                public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
                    JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
                    talentData td = (talentData)o;
                    int[] val = Data.get_CORRES(td.getValue());
                    switch (val[0]) {
                        case Data.PC_AB:
                            for (int i = 0; i < Data.ABI_TOT; i++)
                                if (((val[1] >> i) & 1) == 1) {
                                    jl.setIcon(UtilPC.getIcon(0, i));
                                }
                            break;
                        case Data.PC_P:
                        case Data.PC_IMU:
                            jl.setIcon(UtilPC.getIcon(1, val[1]));
                            break;
                        case Data.PC_BASE:
                            jl.setIcon(UtilPC.getIcon(4, val[1]));
                            break;
                        case Data.PC_TRAIT:
                            jl.setIcon(UtilPC.getIcon(3, val[1]));
                            break;
                    }
                    return jl;
                }
            });
        }
    }

    static class TalentTable extends Page {

        private final PCoinEditTable par;
        private final ArrayList<JL> chance = new ArrayList<>();
        private final ArrayList<Component> tchance = new ArrayList<>(); //Holds JBTN for ID, JTG for booleans, and JTF for int/double
        private int[] curData = null;

        private int cind = -1;
        protected JBTN btn;

        protected TalentTable(PCoinEditTable p) {
            super(p);
            par = p;
        }

        @Override
        protected void resized(int x, int y) {
            for (int i = 0; i < chance.size(); i++) {
                set(chance.get(i), x, y, 0, i * 50, par.getW() / 2, 50);
                set(tchance.get(i), x, y, par.getW() / 2, i * 50, (par.getW() / 2) - 15, 50);
            }
            setPreferredSize(size(x, y, par.getW(), getH()).toDimension());
        }

        public void setTalent(int[] pdata, boolean check) {
            if (check && curData != null && pdata != null && curData[0] == pdata[0] && curData[1] == pdata[1])
                return;
            curData = pdata;

            while (!chance.isEmpty()) {
                int s = chance.size() - 1;
                remove(chance.get(s));
                remove(tchance.get(s));
                chance.remove(s);
                tchance.remove(s);
            }
            if (curData == null || curData[0] == -1)
                return;

            int maxLv = par.unit.pcoin.info.get(par.talent)[1];
            if (pdata[0] == Data.PC_BASE) {
                String text = par.ctypes.getSelectedValue().toString()
                        + (pdata[1] <= Data.PC2_SPEED || pdata[1] == Data.PC2_HB || pdata[1] == Data.PC2_RNG ? "+ " : "")
                        + (pdata[1] == Data.PC2_TBA ? "- " : "")
                        + (pdata[1] <= Data.PC2_ATK || pdata[1] == Data.PC2_TBA ? "%" : "");
                JL[] stTxts = maxLv == 1 ? new JL[]{new JL(text)} : new JL[]{new JL(text + " (Lv1)"), new JL(text + " (Lv" + maxLv + ")")};
                for (int i = 0; i < stTxts.length; i++) {
                    JL stTxt = stTxts[i];
                    add(stTxt);
                    chance.add(stTxt);
                    JTF num = new JTF("" + (int)(par.unit.pcoin.info.get(par.talent)[2 + i] * (pdata[1] == Data.PC2_COST ? 1.5 : 1.0)));
                    int fi = i + 2;
                    num.setLnr(c -> {
                        int[] v = CommonStatic.parseIntsN(num.getText().trim());
                        if (v.length == 0 || v[0] < 0) {
                            num.setText("" + par.unit.pcoin.info.get(par.talent)[fi]);
                            return;
                        }
                        int ind = fi % 2 == 0 ? 1 : -1;
                        int w = maxLv > 1 ? v.length > 1 && v[1] >= 0 ? v[1] : par.unit.pcoin.info.get(par.talent)[fi+ind] : v[0];
                        if (pdata[1] == Data.PC2_COST) {
                            v[0] = (int) (v[0] / 1.5);
                            if (v.length > 1)
                                w = (int) (w / 1.5);
                        } else if (pdata[1] == Data.PC2_TBA) {
                            v[0] = Math.min(v[0], 100);
                            w = Math.min(w, 100);
                        }
                        par.unit.pcoin.info.get(par.talent)[fi + (ind == 1 ? 0 : ind)] = Math.min(v[0], w);
                        par.unit.pcoin.info.get(par.talent)[fi + (ind != 1 ? 0 : ind)] = Math.max(v[0], w);
                        num.setText("" + par.unit.pcoin.info.get(par.talent)[fi]);
                        if (maxLv > 1)
                            ((JTF)tchance.get(fi-2+ind)).setText("" + par.unit.pcoin.info.get(par.talent)[fi+ind]);
                        par.unit.pcoin.update();
                    });
                    add(num);
                    tchance.add(num);
                }
            } else if (pdata[0] == Data.PC_P) {
                int fieldTOT = par.unit.pcoin.info.get(par.talent).length - 3;
                ProcLang.ItemLang lang = ProcLang.get().get(pdata[1]);
                String[] langText = lang.list();
                int offset = pdata.length >= 3 ? pdata[2] * 2 : 0, penalty = 0;
                Field[] pfs = par.unit.getProc().getArr(pdata[1]).getAllFields();

                for (int i = 0; i < fieldTOT; i++) {
                    if (i % 2 == 1 && maxLv == 1)
                        continue;

                    int ri = (i + offset) / 2;
                    JL stTxt = new JL(lang.get(langText[ri]).getNameValue());
                    add(stTxt);
                    chance.add(stTxt);

                    int fi = (i + offset) + 2, val = par.unit.pcoin.info.get(par.talent)[fi];
                    try {
                        Field f = pfs[ri];
                        if (f.getType().equals(int.class) || f.getType().equals(float.class) || f.getType().equals(double.class)) {
                            if (maxLv > 1)
                                stTxt.setText(stTxt.getText() + " (Lv" + (i % 2 == 1 ? maxLv : "1") + ")");
                            JTF num = new JTF("" + val);
                            add(num);
                            tchance.add(num);
                            int fpen = penalty;
                            num.setLnr(c -> {
                                int[] v = CommonStatic.parseIntsN(num.getText().trim());
                                if (v.length == 0 || par.unit.pcoin.info.get(par.talent).length <= fi) {
                                    num.setText("" + val);
                                    return;
                                }
                                int ind = fi % 2 == 0 ? 1 : -1;
                                par.unit.pcoin.info.get(par.talent)[fi] = v[0];
                                if (maxLv > 1 && v.length > 1)
                                    par.unit.pcoin.info.get(par.talent)[fi + ind] = v[1];
                                par.unit.pcoin.info.set(par.talent, par.unit.getProc().getArr(pdata[1]).setTalent(par.unit.pcoin.info.get(par.talent)));
                                num.setText("" + par.unit.pcoin.info.get(par.talent)[fi]);
                                if (maxLv > 1)
                                    ((JTF) tchance.get(fi - 2 + ind - fpen)).setText("" + par.unit.pcoin.info.get(par.talent)[fi + ind]);
                                par.unit.pcoin.update();
                            });
                        } else if (f.getType().equals(boolean.class)) {
                            JTG tgl = new JTG("Apply");
                            tgl.setSelected(val != 0);
                            add(tgl);
                            tchance.add(tgl);
                            tgl.setLnr(r -> par.unit.pcoin.info.get(par.talent)[fi] = par.unit.pcoin.info.get(par.talent)[fi + 1] = tgl.isSelected() ? 1 : 0);
                            penalty++;
                            i++;
                        } else {
                            JBTN jid = new JBTN();
                            if (val != 0)
                                jid.setText((val > 0 ? UserProfile.getBCData() : par.unit.getPack().getPack()).units.get(Math.abs(val)-1).toString());
                            else
                                jid.setText("(N/A)");
                            add(jid);
                            tchance.add(jid);
                            jid.setLnr(l -> {
                                cind = fi;
                                btn = jid;
                                par.pcedit.tab = this;
                                changePanel(par.pcedit.ufp);
                            });
                            penalty++;
                            i++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        chance.remove(stTxt);
                        remove(stTxt);
                        penalty++;
                        i++;
                    }
                }
            }
        }

        @Override
        public JButton getBackButton() {
            return null;
        }

        private int getH() {
            return chance.size() * 50;
        }

        /**
         * 0 is int, 1 is boolean, 2 is Identifier
         * @param ind
         * @return
         */
        private byte getFieldType(int ind) {
            return (byte)(tchance.get(ind) instanceof JTF ? 0 : tchance.get(ind) instanceof JTG ? 1 : 2);
        }

        protected void renew(AbUnit su) {
            if (su != null) {
                Identifier<AbUnit> s = su.getID();
                par.unit.pcoin.info.get(par.talent)[cind] = (s.id+1) * (s.pack.equals(Identifier.DEF) ? 1 : -1);
                btn.setText(su.toString());
            } else {
                par.unit.pcoin.info.get(par.talent)[cind] = 0;
                btn.setText("(N/A)");
            }
            par.unit.pcoin.info.get(par.talent)[cind + 1] = par.unit.pcoin.info.get(par.talent)[cind];
            par.unit.pcoin.update();
            cind = -1;
            btn = null;
        }
    }

    private static final int MAX_TABLE_H = 400;
    private final CustomUnit unit;
    private final NPList ctypes = new NPList();
    private final JScrollPane stypes = new JScrollPane(ctypes);
    private final JL pCoin = new JL();
    private final JL jPCLV = new JL(0,"Max Lv");
    private final JTF PCoinLV = new JTF();
    private final JL jSpLv = new JL(0,"Required Lv");
    private final JTF superLv = new JTF();
    private final JBTN delet = new JBTN(0,"rem");
    private final PCoinEditPage pcedit;
    private final boolean editable;

    private final TalentTable slots = new TalentTable(this);
    private final JScrollPane sslot = new JScrollPane(slots);

    protected int talent;

    private boolean changing;

    protected PCoinEditTable(PCoinEditPage p, CustomUnit u, int ind, boolean edi) {
        super(p);
        pcedit = p;
        unit = u;
        talent = ind;
        editable = edi;

        ini();
    }

    @Override
    public JButton getBackButton() {
        return null;
    }

    @Override
    protected void resized(int x, int y) {
        set(pCoin, x, y, 0, 0, getW(), 50);
        set(delet, x, y, 0, 50, getW(), 50);
        set(jPCLV, x, y, 0, 100, getW() / 2, 50);
        set(PCoinLV, x, y, getW() / 2, 100, getW() / 2, 50);
        set(jSpLv, x, y, 0, 150, getW() / 2, 50);
        set(superLv, x, y, getW() / 2, 150, getW() / 2, 50);

        int sh = Math.min(MAX_TABLE_H, slots.getH());
        set(sslot, x, y, 0, 200, getW(), sh);
        slots.resized(x, y);
        set(stypes, x, y, 0, 200 + sh, getW(), 900 - sh);
    }

    protected int getW() {
        return pcedit.getPTableWidth();
    }

    private void addListeners() {
        PCoinLV.setLnr(arg0 -> {
            String txt = PCoinLV.getText().trim();
            int v = MathUtil.clip(CommonStatic.parseIntN(txt), 1 , 10);
            unit.pcoin.info.get(talent)[1] = v;
            unit.pcoin.max[talent] = v;
            slots.setTalent(Data.get_CORRES(unit.pcoin.info.get(talent)[0]), false);
            setData();
        });

        superLv.setLnr(arg0 -> {
            int v = Math.min(CommonStatic.parseIntN(superLv.getText().trim()), unit.getPack().unit.max + unit.getPack().unit.maxp);
            if (v < 0)
                v = 0;
            int[] tal = unit.pcoin.info.get(talent);
            tal[tal.length - 1] = v;
            setData();
        });

        delet.addActionListener(arg0 -> {
            changing = true;

            unit.pcoin.info.remove(talent);
            unit.pcoin.max = new int[unit.pcoin.info.size()];

            for (int i = 0; i < unit.pcoin.info.size(); i++)
                unit.pcoin.max[i] = unit.pcoin.info.get(i)[1];

            pcedit.removed(talent);
            changing = false;
        });

        ctypes.addListSelectionListener(evt -> {
            if (changing)
                return;
            changing = true;

            talentData np = ctypes.getSelectedValue();
            if (np == null) {
                changing = false;
                return;
            }
            unit.pcoin.info.get(talent)[0] = np.getValue();

            int[] vals = Data.get_CORRES(np.getValue());
            int[] old = unit.pcoin.info.get(talent);
            int[] neww;

            if (vals[0] == Data.PC_P) {
                int tots = unit.getProc().getArr(vals[1]).getAllFields().length;
                neww = Arrays.copyOf(old, 3 + (tots - (vals.length >= 3 ? vals[2] : 0)) * 2);

                Data.Proc.ProcItem itm = unit.getProc().getArr(vals[1]);
                neww = itm.setTalent(neww);
            } else
                neww = Arrays.copyOf(old, vals[0] == Data.PC_BASE ? 5 : 3);

            neww[neww.length - 1] = old[old.length - 1];
            unit.pcoin.info.set(talent, neww);
            pcedit.setCoinTypes();
            changing = false;
        });
    }

    public void shiftDown() {
        talent--;
    }

    private void ini() {
        add(delet);
        add(jPCLV);
        add(pCoin);
        add(jSpLv);
        add(superLv);
        add(stypes);
        add(PCoinLV);
        add(sslot);
        sslot.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sslot.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setCTypes(unit.pcoin != null && unit.pcoin.info.size() > talent);
        addListeners();
    }

    protected void setCTypes(boolean coin) {
        ArrayList<talentData> available = new ArrayList<>();
        if (coin) {
            for (int i = 1; i < Data.PC_CORRES.length; i++) {
                int[] type = Data.PC_CORRES[i];
                if (type[0] == -1 || type[0] == Data.PC_IMU || (type[0] == 5 && i != 5))
                    continue;
                talentData dat = new talentData(Interpret.PCTX[i], i);
                if (available.contains(dat))
                    break;
                // Verify if another talent is using this value
                boolean unused = true;
                for (int j = 0; j < unit.pcoin.info.size(); j++)
                    if (j != talent && unit.pcoin.info.get(j)[0] == i) {
                        unused = false;
                        break;
                    }

                boolean add = type[0] == Data.PC_BASE;
                if (type[0] == Data.PC_P) {
                    Data.Proc.ProcItem pee = unit.getProc().getArr(type[1]);
                    add = !pee.getFieldName(0).equals("prob") || pee.get(0) < 100;
                } else if (type[0] == Data.PC_AB)
                    add = (unit.abi & type[1]) == 0;
                else if (type[0] == Data.PC_TRAIT)
                    add = !(unit.getTraits().contains(UserProfile.getBCData().traits.get(type[1])) || unit.pcoin.trait.contains(UserProfile.getBCData().traits.get(type[1])));
                else if (type[0] == 5) {
                    Data.Proc p = unit.getPack().maxu() == null ? unit.getProc() : unit.getPack().maxu().getProc();
                    add = p.getArr(type[1]).exists() && p.DEFINC.mult != 0; //Also check talents for this one
                }
                if (add && unused)
                    available.add(dat);
            }
            for (int i = 0; i < Data.PC_CUSTOM.length; i++) {
                int[] type = Data.PC_CUSTOM[i];
                if (type[0] == -1)
                    continue;
                talentData dat = new talentData(Interpret.CCTX[i], -i);
                if (available.contains(dat))
                    break;
                // Verify if another talent is using this value
                boolean unused = true;
                for (int j = 0; j < unit.pcoin.info.size(); j++)
                    if (j != talent && unit.pcoin.info.get(j)[0] == -i) {
                        unused = false;
                        break;
                    }

                boolean add = type[0] == Data.PC_BASE;
                if (type[0] == Data.PC_P) {
                    Data.Proc.ProcItem pee = unit.getProc().getArr(type[1]);
                    add = !pee.getFieldName(0).equals("prob") || pee.get(0) < 100;
                } else if (type[0] == Data.PC_AB)
                    add = (unit.abi & type[1]) == 0;
                if (add && unused)
                    available.add(dat);
            }
            talentData[] td = new talentData[available.size()];
            for (int i = 0; i < td.length; i++)
                td[i] = available.get(i);
            ctypes.setListData(td);
            ctypes.setListIcons();
        } else
            ctypes.setListData(new talentData[0]);
    }

    protected int randomize() {
        ListModel<talentData> listModel = ctypes.getModel();
        return listModel.getElementAt((int)(Math.random() * listModel.getSize())).getValue();
    }

    protected void setData() {
        changing = true;
        boolean pc = unit.pcoin != null && unit.pcoin.info.size() > talent;
        int[] type = pc ? Data.get_CORRES(unit.pcoin.info.get(talent)[0]) : new int[]{-1, 0};
        PCoinLV.setEnabled(pc && editable && (type[0] == Data.PC_P || type[0] == Data.PC_BASE));
        if (!PCoinLV.isEnabled() && pc && editable)
            unit.pcoin.info.get(talent)[1] = 1;

        if (pc) {
            PCoinLV.setText("" + unit.pcoin.max[talent]);
            superLv.setText("" + unit.pcoin.getReqLv(talent));
            int tal = unit.pcoin.info.get(talent)[0];
            ListModel<talentData> listModel = ctypes.getModel();
            for (int i = 0; i < listModel.getSize(); i++)
                if (listModel.getElementAt(i).getValue() == tal) {
                    ctypes.setSelectedIndex(i);
                    break;
                }
            slots.setTalent(Data.get_CORRES(unit.pcoin.info.get(talent)[0]), true);
            setIcon(type);
        } else {
            ctypes.clearSelection();
            slots.setTalent(null, false);
            setIcon(type);
            PCoinLV.setText("");
        }
        ctypes.setEnabled(pc && editable);
        delet.setEnabled(pc && editable);
        changing = false;
        fireDimensionChanged();
    }

    private void setIcon(int[] pdata) {
        if (pdata[0] == -1) {
            pCoin.setText("(None)");
            pCoin.setIcon(null);
        } else if (pdata[0] == Data.PC_IMU || pdata[0] == 5) {
            pCoin.setText(ProcLang.get().get(pdata[1]).full_name);
            pCoin.setIcon(UtilPC.getIcon(1, pdata[1]));
        } else if (pdata[0] == Data.PC_AB) {
            for (int i = 0; i < Data.ABI_TOT; i++) {
                if (((pdata[1] >> i) & 1) == 1) {
                    pCoin.setText(Interpret.SABIS[i]);
                    pCoin.setIcon(UtilPC.getIcon(0, i));
                    break;
                }
            }
        } else if (pdata[0] == Data.PC_BASE) {
            String text = ctypes.getSelectedValue().toString()
                    + (pdata[1] <= Data.PC2_SPEED || pdata[1] == Data.PC2_HB || pdata[1] == Data.PC2_RNG ? "+ " : "")
                    + (pdata[1] == Data.PC2_TBA ? "- " : "")
                    + (pdata[1] <= Data.PC2_ATK || pdata[1] == Data.PC2_TBA ? "%" : "");
            pCoin.setText(text);
            pCoin.setIcon(UtilPC.getIcon(4, pdata[1]));
        } else if (pdata[0] == Data.PC_P) {
            pCoin.setText(ProcLang.get().get(pdata[1]).full_name);
            pCoin.setIcon(UtilPC.getIcon(1, pdata[1]));
        } else {
            pCoin.setText(Interpret.TRAIT[pdata[1]]);
            pCoin.setIcon(UtilPC.getIcon(3, pdata[1]));
        }
    }
}