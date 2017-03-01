package com.th.eoss.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by wisarut on 4/10/2559.
 */

public class SETFINComparator implements Comparator<String> {

    protected Map<String, SETFIN> base;
    protected String valueName;

    public SETFINComparator(Map<String, SETFIN> base, String valueName) {
        this.base = base;
        this.valueName = valueName;
    }

    public int compare(String a, String b) {
        return 0;
    }

    public static class AscendingFloatValueComparator extends SETFINComparator {

        public AscendingFloatValueComparator(Map<String, SETFIN> base, String valueName) {
            super(base, valueName);
        }

        public int compare(String a, String b) {
            float left, right;
            left = base.get(a).getFloatValue(valueName);
            right = base.get(b).getFloatValue(valueName);
            if (left <= right) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static class DecendingFloatValueComparator extends SETFINComparator {

        public DecendingFloatValueComparator(Map<String, SETFIN> base, String valueName) {
            super(base, valueName);
        }

        public int compare(String a, String b) {
            float left, right;
            left = base.get(a).getFloatValue(valueName);
            right = base.get(b).getFloatValue(valueName);
            if (left >= right) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static class AscendingLongValueComparator extends SETFINComparator {

        public AscendingLongValueComparator(Map<String, SETFIN> base, String valueName) {
            super(base, valueName);
        }

        public int compare(String a, String b) {
            float left, right;
            left = base.get(a).getLongValue(valueName);
            right = base.get(b).getLongValue(valueName);
            if (left <= right) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static class DecendingLongValueComparator extends SETFINComparator {

        public DecendingLongValueComparator(Map<String, SETFIN> base, String valueName) {
            super(base, valueName);
        }

        public int compare(String a, String b) {
            float left, right;
            left = base.get(a).getLongValue(valueName);
            right = base.get(b).getLongValue(valueName);
            if (left >= right) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static class AscendingSymbolValueComparator extends SETFINComparator {

        public AscendingSymbolValueComparator(Map<String, SETFIN> base, String valueName) {
            super(base, valueName);
        }

        public int compare(String a, String b) {
            String left, right;
            left = base.get(a).symbol();
            right = base.get(b).symbol();
            return left.compareTo(right);
        }
    }

    public static class DecendingSymbolValueComparator extends SETFINComparator {

        public DecendingSymbolValueComparator(Map<String, SETFIN> base, String valueName) {
            super(base, valueName);
        }

        public int compare(String a, String b) {
            String left, right;
            left = base.get(a).symbol();
            right = base.get(b).symbol();
            return right.compareTo(left);
        }
    }
}

