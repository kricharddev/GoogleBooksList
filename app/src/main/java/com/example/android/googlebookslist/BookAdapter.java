package com.example.android.googlebookslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by kyle on 12/27/16.
 */

public class BookAdapter extends ArrayAdapter<BookStrings> {

    // Create a new {@link BookAdapter} object.
    public BookAdapter(Context context, ArrayList<BookStrings> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_items, parent, false);
        }

        // Get the {@link Book} object located at this position in the list
        BookStrings currentBook = getItem(position);

        // Find the TextView in the list_items.xml layout with the ID title
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        // Setting text of .title
        titleTextView.setText(currentBook.getTitle());

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        // Setting text of .title
        authorTextView.setText(currentBook.getAuthor());

        // Finding the TextView in the list_items.xml layout with the ID rating
        TextView ratingTextView = (TextView) listItemView.findViewById(R.id.rating);
        // Setting the text of .rating
        ratingTextView.setText(currentBook.getRating());

        // Finding the TextView in list_items.xml layout with ID of date
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        // Setting the text of .date
        dateTextView.setText(currentBook.getDate());

        return listItemView;
    }
}
