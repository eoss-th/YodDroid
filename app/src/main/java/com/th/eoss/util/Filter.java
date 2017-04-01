package com.th.eoss.util;

/**
 * Created by wisarut on 4/10/2559.
 */

public abstract class Filter {

    protected String meanValueName;

    protected float defaultFilterValue;

    private Filter(String meanValueName, float defaultFilterValue) {

        this.meanValueName = meanValueName;
        this.defaultFilterValue = defaultFilterValue;

    }

    public Filter(float defaultFilterValue) {
        this(null, defaultFilterValue);
    }

    public Filter(String meanValueName) {
        this(meanValueName, 0);
    }

    public abstract boolean isValid(float value);

    public static class LowerOrEqualThanFilter extends Filter {

        public LowerOrEqualThanFilter(String meanValueName) {
            super(meanValueName);
        }

        public LowerOrEqualThanFilter(float defaultFilterValue) {
            super(defaultFilterValue);
        }

        public boolean isValid (float value) {

            if ( meanValueName!=null && Mean.mean(meanValueName)!=null )
                return value <= Mean.mean(meanValueName).value();

            return value <= defaultFilterValue;
        }
    }

    public static class HigherOrEqualThanFilter extends Filter {

        public HigherOrEqualThanFilter(String meanValueName) {
            super(meanValueName);
        }

        public HigherOrEqualThanFilter(float defaultFilterValue) {
            super(defaultFilterValue);
        }

        public boolean isValid (float value) {

            if ( meanValueName!=null && Mean.mean(meanValueName)!=null )
                return value >= Mean.mean(meanValueName).value();

            return value >= defaultFilterValue;
        }
    }
}

