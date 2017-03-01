package com.th.eoss.sevenyods;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

public class SETFINFilterToggleButton extends ToggleButton {

    private static Map<String, Filter> filterMap = new HashMap<>();
    private static Map<String, Boolean> sortMap = new HashMap<>();

    public SETFINFilterToggleButton(Context context, String text) {
        super(context);

        setText(text);
        setTextOff(text);
        setTextOn(text);

        update();
    }

    public static void toggleSort(SETFINFilterToggleButton toggleButton) {

        String textOff = toggleButton.getTextOff().toString();
        Boolean sort = sortMap.get(textOff);

        sortMap.clear();
        if (sort==null || !sort) {
            sortMap.put(textOff, true);
        } else {
            sortMap.put(textOff, false);
        }

        toggleButton.invalidate();
    }

    public static boolean isValid (SETFIN set) {
        Filter filter;
        Set<String> valueNames = filterMap.keySet();
        for (String valueName:valueNames) {
            filter = filterMap.get(valueName);
            if (!filter.isValid(set.getFloatValue(valueName), Mean.mean(valueName).value())) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, SETFIN> sort(Map<String, SETFIN> map) {
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
            } else if (valueName.equals("Pay Date")) {
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

    public static Dialog buildFilterDialog (Activity activity, final SETFINFilterToggleButton toggleButton, final FilterToggleButtonListener filterToggleButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String textOff = toggleButton.getTextOff().toString();
        builder.setTitle(textOff + " Filter");

        if (filterMap.get(textOff)!=null) {
            builder.setItems(new String[]{"Clear", "Cancel"}, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if ( which==0 ) {
                        filterMap.remove(textOff);
                    }
                    if (filterToggleButtonListener!=null)
                        filterToggleButtonListener.onChange();
                }
            });
        } else {
            String [] filters;
            final Filter filter;

            if ( SETFIN.LOW_IS_BETTER.contains(textOff) ) {
                filters = new String[] {"Lower than Average", "Cancel"};
                filter = new Filter.LowerOrEqualThanFilter();
            } else if (SETFIN.HIGH_IS_BETTER.contains(textOff)) {
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
                            filterMap.put(textOff, filter);
                        }
                        if (filterToggleButtonListener!=null)
                            filterToggleButtonListener.onChange();
                    }
                });
            }
        }
        return builder.create();
    }

    public void update() {
        Filter filter = filterMap.get(getTextOff());
        if ( filter!=null ) {
            if (filter instanceof Filter.LowerOrEqualThanFilter) {
                setTextOn(getTextOff() + " <");
            } else {
                setTextOn(getTextOff() + " >");
            }
        } else {
            setTextOn(getTextOff().toString());
        }

        Boolean sort = sortMap.get(getTextOff());

        setChecked(filter!=null || sort !=null);
    }

    public interface FilterToggleButtonListener {
        void onChange();
    }

    public static boolean isFilter(String text) {
        return SETFIN.LOW_IS_BETTER.contains(text) || SETFIN.HIGH_IS_BETTER.contains(text);
    }

    public static void clear() {
        sortMap.clear();
        filterMap.clear();
    }
}
