package com.th.eoss.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by wisarut on 4/10/2559.
 */

public class SETSummaryComparator implements Comparator<String> {

    protected Map<String, SETSummary> base;
    protected String valueName;

    public SETSummaryComparator(Map<String, SETSummary> base, String valueName) {
        this.base = base;
        this.valueName = valueName;
    }

    public int compare(String a, String b) {
        return 0;
    }

    public static class AscendingFloatValueComparator extends SETSummaryComparator {

        public AscendingFloatValueComparator(Map<String, SETSummary> base, String valueName) {
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

    public static class DecendingFloatValueComparator extends SETSummaryComparator {

        public DecendingFloatValueComparator(Map<String, SETSummary> base, String valueName) {
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

    public static class AscendingLongValueComparator extends SETSummaryComparator {

        public AscendingLongValueComparator(Map<String, SETSummary> base, String valueName) {
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

    public static class DecendingLongValueComparator extends SETSummaryComparator {

        public DecendingLongValueComparator(Map<String, SETSummary> base, String valueName) {
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

    public static class AscendingSymbolValueComparator extends SETSummaryComparator {

        public AscendingSymbolValueComparator(Map<String, SETSummary> base, String valueName) {
            super(base, valueName);
        }

        public int compare(String a, String b) {
            String left, right;
            left = base.get(a).symbol();
            right = base.get(b).symbol();
            return left.compareTo(right);
        }
    }

    public static class DecendingSymbolValueComparator extends SETSummaryComparator {

        public DecendingSymbolValueComparator(Map<String, SETSummary> base, String valueName) {
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

