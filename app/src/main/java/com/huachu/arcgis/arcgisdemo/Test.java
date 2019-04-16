package com.huachu.arcgis.arcgisdemo;

import java.util.List;

public class Test {

    /**
     * r : {"code":100000,"desc":"成功"}
     * v : [{"id":373,"level":1,"name":"河北省","children":[{"id":374,"level":2,"name":"廊坊市","pid":373}]}]
     */

    private RBean r;
    private List<VBean> v;

    public RBean getR() {
        return r;
    }

    public void setR(RBean r) {
        this.r = r;
    }

    public List<VBean> getV() {
        return v;
    }

    public void setV(List<VBean> v) {
        this.v = v;
    }

    public static class RBean {
        /**
         * code : 100000
         * desc : 成功
         */

        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public static class VBean {
        /**
         * id : 373
         * level : 1
         * name : 河北省
         * children : [{"id":374,"level":2,"name":"廊坊市","pid":373}]
         */

        private int id;
        private int level;
        private String name;
        private List<ChildrenBean> children;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ChildrenBean> getChildren() {
            return children;
        }

        public void setChildren(List<ChildrenBean> children) {
            this.children = children;
        }

        public static class ChildrenBean {
            /**
             * id : 374
             * level : 2
             * name : 廊坊市
             * pid : 373
             */

            private int id;
            private int level;
            private String name;
            private int pid;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }
        }
    }
}
