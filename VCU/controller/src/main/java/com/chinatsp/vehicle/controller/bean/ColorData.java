package com.chinatsp.vehicle.controller.bean;

import java.util.ArrayList;
import java.util.List;

public class ColorData {

    public List<ColorBean> ColorList() {
        List<ColorBean> beans = new ArrayList();
        beans.add(new ColorBean(255,1,1));
        beans.add(new ColorBean(255,14,1));
        beans.add(new ColorBean(255,26,1));
        beans.add(new ColorBean(255,38,1));
        beans.add(new ColorBean(255,51,1));
        beans.add(new ColorBean(255,63,1));
        beans.add(new ColorBean(255,74,1));
        beans.add(new ColorBean(255,87,1));
        beans.add(new ColorBean(255,100,1));
        beans.add(new ColorBean(255,118,1));
        beans.add(new ColorBean(255,138,1));
        beans.add(new ColorBean(255,157,1));
        beans.add(new ColorBean(255,177,1));
        beans.add(new ColorBean(255,197,1));
        beans.add(new ColorBean(255,217,1));
        beans.add(new ColorBean(255,236,5));
        beans.add(new ColorBean(255,239,45));
        beans.add(new ColorBean(255,243,83));
        beans.add(new ColorBean(255,245,122));
        beans.add(new ColorBean(255,247,161));
        beans.add(new ColorBean(255,241,200));
        beans.add(new ColorBean(255,254,240));
        beans.add(new ColorBean(235,249,246));
        beans.add(new ColorBean(201,240,232));
        beans.add(new ColorBean(200,240,231));
        beans.add(new ColorBean(134,222,203));
        beans.add(new ColorBean(100,213,190));
        beans.add(new ColorBean(66,204,175));
        beans.add(new ColorBean(33,195,161));
        beans.add(new ColorBean(0,186,147));
        beans.add(new ColorBean(5,189,142));
        beans.add(new ColorBean(8,192,136));
        beans.add(new ColorBean(13,195,131));
        beans.add(new ColorBean(17,198,126));
        beans.add(new ColorBean(21,201,120));
        beans.add(new ColorBean(26,205,117));
        beans.add(new ColorBean(28,209,138));
        beans.add(new ColorBean(30,215,159));
        beans.add(new ColorBean(32,219,180));
        beans.add(new ColorBean(34,224,201));
        beans.add(new ColorBean(36,229,222));
        beans.add(new ColorBean(39,234,243));
        beans.add(new ColorBean(38,227,254));
        beans.add(new ColorBean(33,205,253));
        beans.add(new ColorBean(28,183,253));
        beans.add(new ColorBean(23,161,251));
        beans.add(new ColorBean(18,139,250));
        beans.add(new ColorBean(13,116,248));
        beans.add(new ColorBean(8,94,247));
        beans.add(new ColorBean(21,89,248));
        beans.add(new ColorBean(37,87,249));
        beans.add(new ColorBean(55,86,250));
        beans.add(new ColorBean(72,84,252));
        beans.add(new ColorBean(88,83,253));
        beans.add(new ColorBean(105,81,254));
        beans.add(new ColorBean(122,79,254));
        beans.add(new ColorBean(138,72,249));
        beans.add(new ColorBean(155,66,244));
        beans.add(new ColorBean(172,60,239));
        beans.add(new ColorBean(189,53,234));
        beans.add(new ColorBean(206,47,229));
        beans.add(new ColorBean(222,40,223));
        beans.add(new ColorBean(238,33,218));
        beans.add(new ColorBean(255,27,213));
        return beans;
    }


    public class ColorBean {
        private int r;
        private int g;
        private int b;

        public ColorBean(int r, int g, int b) {
            setR(r);
            setG(g);
            setB(b);
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getG() {
            return g;
        }

        public void setG(int g) {
            this.g = g;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }
    }


}
