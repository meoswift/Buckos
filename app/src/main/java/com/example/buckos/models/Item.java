package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@ParseClassName("Item")
@Parcel(analyze={Item.class})
public class Item extends ParseObject {

}
