package com.seoulprojet.seoulpoem.model;

import java.util.ArrayList;

/**
 * Created by minjeong on 2017-10-15.
 */

public class ReadingPoem {

    public String status;
    public Articleinfo article;

    public class Articleinfo{

        public Settinginfo setting;
        public String content;
        public String tags;
        public String background;
        public String inform;
        public String date;
        public String title;
        public String photo;
        public Userinfo user;
        public int modifiable;

    }

    public class Settinginfo{
        public int font_size;
        public int bold;
        public int inclination;
        public int underline;
        public int color;
        public int sort;
    }

    public class Userinfo{
        public String profile;
        public String pen_name;
        public ArrayList<Otherinfo> others;
    }

    public class Otherinfo{
        public int idarticles;
        public String photo;
    }
}
