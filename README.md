Original App Design Project - Buckos
===

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)

## Overview
### Description
Android social network app that allows users to track their bucket list, keep a travel journal, and share their accomplishments to their friends. 

### App Evaluation
- **Description**: Organizer - social network app that allows users to track their bucket list, keep a travel journal, and share their accomplishments to their friends. 
- **Category**: Personal organizer
- **Mobile**: User can take photos with camera when they want to record something they accomplished in their list.
- **Story**: Allow users to keep track of their bucket list and share their journal/story through each item they completed.
- **Market:** Anyone who has a list of things they would like to complete can enjoy this app. 
- **Habit:** People can use the app regularly to explore places they'd like to visit in different cities, books they'd like to read, movies they'd like to see... (from a given database). They can keep track of multiple lists and even add their own customized category. User can check their feed and get inspired by what their friends have achieved in their bucket list.
- **Scope:** V1 would allow user to create an account, login/logout of the app, create a list and add items to list. V2 would incorporate the Explore cities feature, where user gets a list of places to visit when they search for a city. User can add places to a new list. V3 would allow user to edit items and document their experience on a completed item. V4 would let user search for users and view a feed of activities.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can create a profile.
* User can log in/ log out of the app.
* User can create multiple  bucket lists with different titles.
    * User can create their own list with personalized items.
    * User can create a list from suggested traveling places.
* User can search for any city they want to visit and based on the query, a list of places to visit will be generated for them. 
    * User can add items from generated list to a new bucket list, whose title will be city name by default.
* User can add, remove, edit items in a list, and edit the title of the list.
    * When user add a new item to list, they can add a note to it. 
    * When user complete an item, they can add a description, a date, and photos to document their experience. 
* User can see their profile with their bucket lists. 
    * User can see an overview of their progress on each list (maybe the percentage).
    * When they click on a list, they will see the completed and incomplete items. 
* User can search for other people based on their username.
* User can follow/unfollow other users.
* User can then share a completed item to feed.
* User can view a feed of other people's posts about their accomplishments.
* User can like posts from feed.
* User can add an item from feed to their list and customize it.

**Optional Nice-to-have Stories**
* User can search a list by title (in case they have too many).
* User can add their own place if they cannot find a place they want to visit in the generated list of places.
* User can get more suggestions other than travel...maybe books, movies, etc.
* User see someone's profile when they search for them or click on profile pic.


### 2. Screen Archetypes

* Login Screen
   * User can login
* Registration screen
   * User can create an account
* Home 
    * User can view feed of friends' posts about their experience with a completed item
    * User can tap to like the post
    * User can double tap to add that item to their list (bonus)
* Create new list  
    * User can add title, description of list 
    * User can add new items to the list 
* User profile 
    * User can view a list of their bucket lists.
    * User can see percentage completed on each list (bonus).
* List Details
    * User can navigate between two tabs: complete and incomplete items.
    * User can swipe left to delete an item.
    * Incomplete items tab:
        * User can click on icons on an item to edit or mark an item complete.
        * User can add a new item to list
        * User can edit list title (bonus)
    * Complete items tab:
        * User can tap on item to view details.
    * User can search list by title (bonus)
* Add new Item 
    * User can add name, a note, and time to complete by (bonus).
* Edit Item Screen
    * User can edit incomplete item's title, note, and date to complete by (bonus).
* Mark Completed Screen 
    * User cannot edit item title.
    * User can add a note/description on their experience
    * User can take photos with their camera or add photos from library.
* Completed Item Detail Screen
    * User can view title, note, photos attached to the item when they mark it complete.
* Explore Cities Screen 
    * User can search for a city to visit
    * User can view a list of suggested places and activities
    * User can click "+" to add an item to a new generated list 
    * User can click on a place to view details about that place (bonus)
* Search
    * User can search for people by username
    * Follow/unfollow other user  

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home 
* Search  
* Create new list 
* Explore Cities 
* User profile

**Flow Navigation** (Screen to Screen)

* Login Screen / Registration Screen
    --> Home 
* Create new list
    --> List Details 
* List Details
    * If user is in Incomplete tab:
    --> Add new item Screen if user clicks on floating action button
    --> Edit Item Screen if user clicks on edit button in an item
    --> Mark Complete Item Screen if user clicks on ☑️ icon 
    * If user is in Complete tab:
    --> Complete item detail screen if user click on an item 
* Explore Cities 
    --> None
* User profile 
    --> List Details
* Search 
    --> None
* Home 
    --> None    
    
## Wireframes
Rough hand sketch of my app.

<img src="https://i.imgur.com/aOpzwmy.png" width=700>    
    
