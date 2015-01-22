package no.inmeta.iwsocket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kubkaray on 22.01.2015.
 */

public class StaticList {
    public static final List<String> list=new ArrayList<String>();
    private static StaticList staticList = new StaticList();
    private StaticList() {}
    public static StaticList getInstance() {
        return staticList;
    }
}
