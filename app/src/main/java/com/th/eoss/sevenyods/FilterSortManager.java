package com.th.eoss.sevenyods;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.ToggleButton;

import com.th.eoss.util.Filter;
import com.th.eoss.util.Mean;
import com.th.eoss.util.SETFIN;
import com.th.eoss.util.SETFINComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by wisarut on 4/10/2559.
 */

public class FilterSortManager {

    private static Map<String, FilterSortManager> instanceMap = new HashMap<>();

    private Map<String, Filter> filterMap;
    private Map<String, Boolean> sortMap;

    private FilterSortManager () {
        filterMap = new HashMap<>();
        sortMap = new HashMap<>();
    }

    public static FilterSortManager instance(String groupName) {

        FilterSortManager manager = instanceMap.get(groupName);

        if ( manager==null ) {
            manager = new FilterSortManager();
            instanceMap.put(groupName, manager);
        }

        return manager;
    }

    public void toggleSort(String valueName) {

        Boolean sort = sortMap.get(valueName);

        sortMap.clear();
        if (sort==null || !sort) {
            sortMap.put(valueName, true);
        } else {
            sortMap.put(valueName, false);
        }

    }

    public void put(String valueName, Filter filter) {
        filterMap.put(valueName, filter);
    }

    public boolean isValid (SETFIN set) {
        Filter filter;
        Set<String> valueNames = filterMap.keySet();

        float conditionValue;
        for (String valueName:valueNames) {
            filter = filterMap.get(valueName);

            if (valueName.equals("Predict %"))
                conditionValue = 52;
            else
                conditionValue = Mean.mean(valueName).value();

            if (!filter.isValid(set.getFloatValue(valueName), conditionValue)) {
                return false;
            }
        }
        return true;
    }

    public Map<String, SETFIN> sort(Map<String, SETFIN> map) {
        Set<String> valueNames = sortMap.keySet();
        Boolean sort;
        for (String valueName:valueNames) {
            sort = sortMap.get(valueName);
            if (valueName.equals("Symbols")) {
                if (sort) {
                    return new TreeMap<>(new SETFINComparator.DecendingSymbolValueComparator(map, valueName));
                } else {
                    return new TreeMap<>(new SETFINComparator.AscendingSymbolValueComparator(map, valueName));
                }
            } else if (valueName.equals("XD")) {
                if (sort) {
                    return new TreeMap<>(new SETFINComparator.DecendingLongValueComparator(map, valueName));
                } else {
                    return new TreeMap<>(new SETFINComparator.AscendingLongValueComparator(map, valueName));
                }
            } else {
                if (sort) {
                    return new TreeMap<>(new SETFINComparator.DecendingFloatValueComparator(map, valueName));
                } else {
                    return new TreeMap<>(new SETFINComparator.AscendingFloatValueComparator(map, valueName));
                }
            }
        }
        return null;
    }

    public Dialog buildFilterDialog (Activity activity, final String valueName, String valueTitle, final FilterToggleButtonManagerListener filterToggleButtonManagerListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(valueTitle + " Filter");

        if (filterMap.get(valueName)!=null) {
            builder.setItems(new String[]{"Clear", "Cancel"}, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if ( which==0 ) {
                        filterMap.remove(valueName);
                    }
                    if (filterToggleButtonManagerListener !=null)
                        filterToggleButtonManagerListener.onChange();
                }
            });
        } else {
            String [] filters;
            final Filter filter;

            if ( SETFIN.LOW_IS_BETTER.contains(valueName) ) {
                filters = new String[] {"Lower than Average", "Cancel"};
                filter = new Filter.LowerOrEqualThanFilter();
            } else if (SETFIN.HIGH_IS_BETTER.contains(valueName)) {
                filters = new String[] {"Higher than Average", "Cancel"};
                filter = new Filter.HigherOrEqualThanFilter();
            } else {
                filters = null;
                filter = null;
            }

            if (filters!=null && filter!=null) {
                builder.setItems(filters, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if ( which==0 ) {
                            filterMap.put(valueName, filter);
                        }
                        if (filterToggleButtonManagerListener !=null)
                            filterToggleButtonManagerListener.onChange();
                    }
                });
            }
        }
        return builder.create();
    }

    public void update(String valueName, ToggleButton button) {
        Filter filter = filterMap.get(valueName);
        if ( filter!=null ) {
            if (filter instanceof Filter.LowerOrEqualThanFilter) {
                button.setTextOn(button.getTextOff() + " <");
            } else {
                button.setTextOn(button.getTextOff() + " >");
            }
        } else {
            button.setTextOn(button.getTextOff().toString());
        }

        Boolean sort = sortMap.get(valueName);

        button.setChecked(filter!=null || sort !=null);
    }

    public interface FilterToggleButtonManagerListener {
        void onChange();
    }

    public boolean isFilter(String text) {
        return SETFIN.LOW_IS_BETTER.contains(text) || SETFIN.HIGH_IS_BETTER.contains(text);
    }

    public void clear() {
        sortMap.clear();
        filterMap.clear();
    }
}
