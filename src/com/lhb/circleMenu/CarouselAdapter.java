package com.lhb.circleMenu;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CarouselAdapter extends BaseAdapter {

    String[] itemsArray = { "A12", "A1", "A2", "A3", "A4", "B", "B1", "B2",
            "C", "D", "E", "F", "G", "H", "I", "J", "K", "LLL", "M", "N", "O",
            "P", "Q" };
    private Map<Object, Integer> letterMaps = new HashMap<Object, Integer>();

    private LayoutInflater mInflater;

    public CarouselAdapter(Context c) {
        mInflater = LayoutInflater.from(c);
        char mHasGot = 0;
        letterMaps.clear();
        for (int i = 0; i < itemsArray.length; i++) {
            String pinyin = itemsArray[i];
            pinyin = pinyin.toUpperCase();
            char firstChar = pinyin.charAt(0);
            if (firstChar >= 'A' && firstChar <= 'Z') {
                if (mHasGot != firstChar) {
                    letterMaps.put(firstChar, Integer.valueOf(i));
                    mHasGot = firstChar;
                }
            }
        }
    }

    public int getLetterPosition(char letter) {
        if (letterMaps.containsKey(letter)) {
            return letterMaps.get(letter);
        } else {
            return -1;
        }
    }

    @Override
    public int getCount() {
        return itemsArray.length;
    }

    @Override
    public Object getItem(int position) {
        return itemsArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.row, null);
        TextView listTextView = (TextView) rowView.findViewById(R.id.itemtext);
        listTextView.setText(itemsArray[position]);
        return rowView;
    }
}
