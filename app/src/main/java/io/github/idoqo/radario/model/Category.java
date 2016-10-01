package io.github.idoqo.radario.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class Category {
    private int id;
    private String name;
    private String description;

    //forum categories, if you are bothered with the irregular capitalization in string values
    //then you prolly haven't read an inch of the other codes...
    public static String CAT_EVERYTHING = "Everything";
    public static String CAT_AMA = "AMA";
    public static String CAT_PROMOTED = "promoted";
    public static String CAT_PRODUCT = "Products";
    public static String CAT_MAKERS = "Makers";
    public static String CAT_DESIGN = "Design";
    public static String CAT_CABAL = "Techcabal";
    public static String CAT_EVENTS = "Events";
    public static String CAT_JOBS = "Jobs";
    public static String CAT_META = "Meta";

    public Category(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //to avoid creating new instances just to get the name
    public static String getnameFromId(int id){
        switch (id){
            case 14:
                return CAT_PROMOTED;
            case 11:
                return CAT_MAKERS;
            case 10:
                return CAT_CABAL;
            case 8:
                return CAT_AMA;
            case 3:
                return CAT_META;
            case 13:
                return CAT_DESIGN;
            case 5:
                return CAT_PRODUCT;
            case 6:
                return CAT_JOBS;
            case 7:
                return CAT_EVENTS;
            case 1:
            default:
                return CAT_EVERYTHING;
        }
    }
}
