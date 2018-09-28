/**
 *  LICENSE
 * 
 *  This file is part of Flyve MDM Inventory Library for Android.
 *
 *  Inventory Library for Android is a subproject of Flyve MDM.
 *  Flyve MDM is a mobile device management software.
 * 
 *  Flyve MDM is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 3
 *  of the License, or (at your option) any later version.
 *
 *  Flyve MDM is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  ---------------------------------------------------------------------
 *  @author    Rafael Hernandez - <rhernandez@teclib.com>
 *  @copyright Copyright Teclib. All rights reserved.
 *  @copyright Copyright FusionInventory.
 *  @license   GPLv3 https://www.gnu.org/licenses/gpl-3.0.html
 *  @link      https://github.com/flyve-mdm/android-inventory-library
 *  @link      http://flyve.org/android-inventory-library/
 *  @link      https://flyve-mdm.com
 *  ---------------------------------------------------------------------
 */

package org.flyve.inventory.categories;


import android.content.Context;

import org.flyve.inventory.CommonErrorType;
import org.flyve.inventory.FILog;
import org.flyve.inventory.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class get all the information of the Memory
 */
public class Memory extends Categories {


    /*
     * The serialization runtime associates with each serializable class a version number,
     * called a serialVersionUID, which is used during deserialization to verify that the sender
     * and receiver of a serialized object have loaded classes for that object that are compatible
     * with respect to serialization. If the receiver has loaded a class for the object that has a
     * different serialVersionUID than that of the corresponding sender's class, then deserialization
     * will result in an  InvalidClassException
     *
     *  from: https://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
     */
	private static final long serialVersionUID = -494336872000892273L;

    // Properties of this component
    private static final String DESCRIPTION = "Memory";
    private static final String MEMINFO = "/proc/meminfo";
    private String[] ramInfo = new String[2];
    private final Context context;

    /**
     * This constructor load the context and the Memory information
     *
     * @param xCtx Context where this class work
     */
    public Memory(Context xCtx) {
        super(xCtx);

        context = xCtx;

        try {
            getRamInfo();
            Category c = new Category("MEMORIES", "memories");

            c.put("DESCRIPTION", new CategoryValue(DESCRIPTION, "DESCRIPTION", "description"));
            c.put("CAPACITY", new CategoryValue(getCapacity(), "CAPACITY", "capacity"));
            c.put("TYPE", new CategoryValue(getType(), "TYPE", "type"));
            c.put("SPEED", new CategoryValue(getSpeed(), "SPEED", "speed"));

            this.add(c);
        } catch (Exception ex) {
            FILog.e(ex.getMessage());
        }
    }

    /**
     *  Get total memory of the device
     * @return String Total Memory
     * @throws IOException return exception
     */
	public String getCapacity() {
        String capacity = "N/A";
        try {
            FileReader fr = null;
            try {
                File f = new File(MEMINFO);
                fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr, 8 * 1024);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("MemTotal")) {
                        String[] parts = line.split(":");
                        String part1 = parts[1].trim();
                        Long memory = Long.valueOf(part1.replaceAll("(.*)\\ kB", "$1"));
                        memory = memory / 1024;
                        capacity = String.valueOf(memory);
                    }
                }

                br.close();
            } catch (IOException e) {
                FILog.e(e.getMessage());
            } finally {
                if (fr != null) {
                    fr.close();
                }
            }
        } catch (Exception ex) {
            FILog.e(FILog.getMessage(context, CommonErrorType.BATTERY_TECHNOLOGY, ex.getMessage()));
        }
        return capacity;
	}

    private void getRamInfo() {
        String infoCat = Utils.getCatInfo("/sys/bus/platform/drivers/ddr_type/ddr_type");
        if (!infoCat.equals("")) {
            splitRamInfo(infoCat);
        } else {
            String infoProp = getRamProp();
            if (infoProp != null){
                splitRamInfo(infoProp);
            } else {
                ramInfo[0] = "N/A";
                ramInfo[1] = "N/A";
            }
        }
    }

    private void splitRamInfo(String info) {
        if (info.contains("_")) {
            String[] partRam = info.split("_", 2);
            ramInfo[0] = partRam[0];
            ramInfo[1] = partRam[1];
        } else {
            ramInfo[0] = info;
            ramInfo[1] = "N/A";
        }
    }

    private String getRamProp() {
        String a = Utils.getSystemProperty("ro.boot.hardware.ddr");
        if (!(a == null || a.isEmpty())) {
            int indexOf = a.indexOf("LPDDR");
            if (indexOf > 0) {
                return a.substring(indexOf);
            }
        }
        return null;
    }

    public String getType() {
        return ramInfo[0];
    }

    public String getSpeed() {
        return ramInfo[1];
    }
}
