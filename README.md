# UserApplicationTGADigital

# Main Features
1. Kotlin
2. MVVM
3. RxJava/RxAndroid
4. LiveData
5. ViewModel
6. Retrofit2
7. Glide

# App Detail
# Main Page
Application contains 2 part. First part is main page which shows default users. It has input field and search button for search users by name. It gets users and sorts first 20 users alphabetically.
I prefer to show only one page and didn't write pagination. I think that there is no need for test case. All observable datas get by helping RxJava/RxAndroid and observs this datas with LiveData.
Users data and search key value set to live data.

# User Detail 
In this page there is some detailed information about a user which clicked in Main Page. Glide is used for show image by url. 

# Warning 
User age doesn't exist in api.
User detail api doesn't exist this's why I haven't used any api request User Detail page. All of user detail data passes to another activity with intent. 
I think that UI and UX is second plan in this test that's why ui/ux is not wonderful. 
