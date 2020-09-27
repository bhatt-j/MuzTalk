package com.example.muztalk;

public class ModelUsers {
    String username,imageURL,id,search,status;

    public ModelUsers(String username, String imageURL, String id, String search, String status) {
        this.username = username;
        this.imageURL = imageURL;
        this.id = id;
        this.search = search;
        this.status = status;
    }

    public ModelUsers()
    {

    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
