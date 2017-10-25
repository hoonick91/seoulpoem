package com.seoulprojet.seoulpoem.model;

import java.util.ArrayList;

/**
 * Created by lynn on 2017-10-15.
 */

public class WriterListResult {
    public int count_authors;
    public ArrayList<AuthorList> authors_list;

    public class AuthorList{
        public String email;
        public String pen_name;
        public String profile;
        public String inform;
        public int type;
    }


}
