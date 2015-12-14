/*
 * Copyright (c)  2006-2007 University Of Maryland
 * All rights  reserved.  
 * Modifications done by Massachusetts General Hospital
 *  
 *  Contributors:
 *  
 *  	Wensong Pan (MGH)
 *		
 */

package edu.harvard.i2b2.timeline.lifelines;

import java.awt.*;

public class infoPanel extends Panel{

    private int width, height;
    private String name, sex;

    private int age;
    private String moreinfo;   // 3/28/98

    protected static Font font = new Font("TimesRoman", Font.BOLD, 12);
    protected FontMetrics fontMetrics = getFontMetrics(font);

    public infoPanel(int width, int height,String name,String gender,int age,String moreinfo) {   // 3/28/98

        this.width = width;
        this.height = height;

        this.name = name;
        sex = gender;
        this.age = age;
        this.moreinfo = moreinfo;   // 3/28/98

        //adminInf=new Button("Admin Info");
        //add(adminInf);
        //adminInf.reshape(275,12,110,23);


        //famHis=new Button("Family History");
        //add(famHis);
        //famHis.reshape(275,42,110,23);

    }

 @Override
public void paint(Graphics g){

        g.setColor(Color.white); // was lightGray, had no effect...
        g.draw3DRect(0,0,width-1,height-1,false);
        g.draw3DRect(1,1,width-3,height-3,false);

        g.setFont(font);
        int currX1 = 15;
        int currH = 20;

        //name = new String("Simpson, Homer");
        g.setColor(Color.blue);
        g.drawString(name, currX1, currH);

        /*g.setColor(Color.black);
        addr = new String("1234 Pirate Street");
        g.drawString(addr, currX1, currH + fontMetrics.getHeight());

        city = new String("Barbary Coast");
        g.drawString(city, currX1, currH + 2*fontMetrics.getHeight());

        phone = new String("(301)-234-6543");
        g.drawString("Phone: " + phone, currX1, currH + 3*fontMetrics.getHeight());

        dob = new String("9/12/35");
        g.drawString("DOB: " + dob, currX2,currH);

        race = new String("Caucasian");
        g.drawString(race, currX2, currH + fontMetrics.getHeight());*/

        //sex = new String("Male");
        //g.drawString(sex, currX2, currH + 2*fontMetrics.getHeight());
        g.drawString(sex, currX1, currH + fontMetrics.getHeight());

        g.drawString(Integer.toString(age),currX1+70, currH + /*2 */ fontMetrics.getHeight());

        g.drawString(moreinfo, currX1, currH + 2 * fontMetrics.getHeight());
        //weight = new String("5'-6\", 100 Lbs");
        //g.drawString(weight, currX2, currH + 3*fontMetrics.getHeight());
    }

}
