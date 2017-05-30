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

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass());
    }

    public abstract boolean isValid(SETFIN set, String meanValueName);

    public static class LowerOrEqualThanFilter extends Filter {

        public LowerOrEqualThanFilter(String meanValueName) {
            super(meanValueName);
        }

        public LowerOrEqualThanFilter(float defaultFilterValue) {
            super(defaultFilterValue);
        }

        public boolean isValid (SETFIN set, String valueName) {

            if ( super.meanValueName!=null && Mean.mean(set.industry + "." + set.sector + "." + super.meanValueName)!=null )
                return set.getFloatValue(super.meanValueName)
                        <= Mean.mean(set.industry + "." + set.sector + "." + super.meanValueName).value()
                        && set.getFloatValue(super.meanValueName) > 0;

            return set.getFloatValue(valueName) <= defaultFilterValue;
        }
    }

    public static class HigherThanFilter extends Filter {

        public HigherThanFilter(String meanValueName) {
            super(meanValueName);
        }

        public HigherThanFilter(float defaultFilterValue) {
            super(defaultFilterValue);
        }

        public boolean isValid (SETFIN set, String valueName) {

            if ( super.meanValueName!=null && Mean.mean(set.industry + "." + set.sector + "." + super.meanValueName)!=null )
                return set.getFloatValue(super.meanValueName)
                        > Mean.mean(set.industry + "." + set.sector + "." + super.meanValueName).value();

            return set.getFloatValue(valueName) > defaultFilterValue;
        }
    }

    public static class HigherOrEqualThanFilter extends Filter {

        public HigherOrEqualThanFilter(String meanValueName) {
            super(meanValueName);
        }

        public HigherOrEqualThanFilter(float defaultFilterValue) {
            super(defaultFilterValue);
        }

        public boolean isValid (SETFIN set, String valueName) {

            if ( super.meanValueName!=null && Mean.mean(set.industry + "." + set.sector + "." + super.meanValueName)!=null )
                return set.getFloatValue(super.meanValueName)
                        >= Mean.mean(set.industry + "." + set.sector + "." + super.meanValueName).value();

            return set.getFloatValue(valueName) >= defaultFilterValue;
        }
    }
}

