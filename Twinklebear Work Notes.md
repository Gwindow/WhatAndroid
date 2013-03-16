# Twinklebear Work Notes
Just a file to track my notes related to working on the WhatAPI since there isn't much of a point
in opening issues on the main repo that only I really need to keep track of.

## Immediate work:
Clean up deprecated API usage in the app. - Leaving this alone, the functions are probably necessary for
backwards compatibility
Bug fixes? - Will read through forum thread and make a list

## Bug Fixes:
#### TorrentGroups without album art:
The App seems to crash sometimes when loading a TorrentGroup with no album art. It seems that this
issue is exclusive to my emulator, on the released version on my phone it just plays the spinning wheel.
Did I break something? I'll have to check through my changes. From checking my previous changes in the file I've only
made typo corrections there. So perhaps this is a result of the library update.

#### Smaller Font Size/Better Display of text
The font is really big, and on every torrent search view/notification listing and etc. causes the
full text to be cut off, the font size should be smaller and the text should wrap to not cut off any
relevant information

The artist name in the notifications window is not displayed. It should be. (also year?) Perhaps for
notifications list Artist - Album - Year - Format, then on some kind of interaction show files, size, time,
snatched, seeders and leechers if we have this information.

The Up/Down/Buffer/Ratio text size is also kind of big, causing the entry's text to run together. Should fix.

Also the Bookmarks window only shows album names - The artist name isn't returned when requesting torrent bookmarks. Why?
Doesn't look like we're given artist name when requesting notifications list either. Options are to either add this
field to be returned, or to perform multiple API requests, one for the torrent and another to get the artist somehow,
I'm not sure if the information we're given is enough to get the artist without 2 API requests. So 2 extra
requests per entry in the list which sounds really really terrible. I'll ask Gwindow if it's possible to get this field
added to the response (artist name and id would be nice)

In the TorrentGroup view it would be nice to show the artist name

#### Login hanging
Sometimes the app sits attempting to login but hangs indefinitely. Why is it doing this? It seems to be
resolved by clearing the cache, but this is something that should be fixed.

Is it possible to display a failed login counter?

It seems people with this issue also have non-alphanumeric characters? Perhaps this is why I have it sometimes too?
Sounds like special characters are to blame. Will have to find out why this is. Maybe the login post request gets funked up?

### Crash after resuming from long period in background
When resuming it will flash some screens before crashing and then crash. This only occurs after long periods of being in
the background and I think may be related to the authkey becoming invalid due to inactivity? (Does what.cd do that?)

If this is the case then after a long period of inactivity the app should resume at the login screen, or auto-login when
resuming to get a valid authkey.

#### Back arrow button on home screen
It should do something or not be shown. My future plan is to have it open the options
panel like on reddit sync, but for now maybe make it quit? If it does nothing it shouldn't be shown.

#### Home screen search box
Clicking the search icon brings up the text box but doesn't focus it to bring up the keyboard, it should be focused

Tried setting <requestfocus /> as a property of the EditText in the layout file but it didn't seem to do it. The home
activity seems a bit odd, I'll have to look at it more.

#### Batch Scanning
feppea report an issue on Galaxy Tablet where he gets a "couldn't write to file" error. I've never used this feature,
will have to investigate.

#### PyWA Feature is unfinished?
I'll have to figure out how to setup PyWA on my seedbox to actually test this, but runagt2 reports that it crashes when
trying to use the feature

#### Barcode scanner not searching correctly
Sometimes when scanning a barcode/entering UPC the search will report that the item isn't found, or maybe
return unrelated results? We're probably getting some wrong data back from how we resolve the barcode information

#### Torrent Search returning null
I seem to be getting a null torrentSearch in the Async task in TorrentSearchActivity, looking into why. Have
tracked the issue down through my WhatAPI Test program. When trying to parse the search result to object from json
we get Expecting number, got: STRING. Now to find out which one is wrong. Found the issue:
in the search response results, the value of groupTime is supposed to be a number, but I seem to be getting a string back
the API says it should return a number on the wiki page and the app running on my phone doesn't seem to crash either.

The scraped JSON response showing the issue:
```json
Scraped: {"status":"success","response":{"currentPage":1,"pages":31,"results":[
{"groupId":72058492,"groupName":"All Punk Rods: A Gearhead Magazine Compilation",
"artist":"Various Artists","tags":["garage","punk","surf"],"bookmarked":false,
"vanityHouse":false,"groupYear":1998,"releaseType":"Compilation",
"groupTime":"1352875841","maxSize":248945626,
```
Where you can see groupTime is a String now. But it should be a number. Why has this changed? Why doesn't the released
app crash with what I can only assume is the same response? This is an API issue so I'm adding a note there too

#### Loading Artist/TorrentGroup/etc has no feedback
It should at least show a spinny circle or something to show that loading is occuring, instead of just sitting there.

#### Make use of remove bookmark API from the bookmark view as well
This way when looking at your list of bookmarks you can remove bookmarks of entries you no longer wish to have bookmarked.

## Other comments
Mention ability to send torrents to seedbox via TransDroid or similar, It seems a lot of people are unaware of the
app's capability to do this and as such are requesting it as a feature (myself included until recently)

I feel like double tapping to open artists/torrents/etc is unnecessary. A single tap should suffice.

## Questions
In fragments.ArtFragment.getReflection it takes a Bitmap image, then sets Bitmap originalImage = image
and proceeds to exclusively use originalImage throughout the code. IntelliJ thinks originalImage is redundant
and I agree. Is there a reason for its existence?

Also it seems that this function is the cause of some crashes, namely originalImage.getWidth, its first usage
results in some NullPointerExceptions. Why? What would be missing if I filtered out cases of image == null?

Why do I sometimes get null for artist name/art or such in the artist api response?

For organizational sake I want to re-organize the styles.xml file to list the styles alphabetically, as it is now
the only hope for navigating it is to use find.

## Feature Addition Ideas
Bookmark button (see below)

It seems that enabling/disabling notifications for an Artist isn't implemented, I'll toss it in

Make it possible to see the WhatStatus page without being logged in. How are you going to check
the status if you have to login to the site to access it but the site is down?
Correction: This is possible through the options menu, I'd like to make it a swipeable fragment later on

There should be an option to Log out

The login.xml layout file could do with some improvements.

When clicking on a What.cd internal link, ie in a forum thread someone posts "go check out torrents.php=?id=TORRENTID"
when you click the link, the app should recognize the what.cd link and respond appropriately. This may be harder than i think it is..
As of now it seems to cause a crash. ActivityNotFoundException. So make it found haha. Or is this in? I called it from a User's page and it crashed
will check more

Copy url of torrent. Do they want the download url? Or the torrent.php url?

### Bookmark option for TorrentGroups (also do for Artists)
#### For TorrentGroups:
I've written a simple Async task that will change the status of the bookmark, so if it's not bookmarked
it bookmarks it, if it is bookmarked it removes the bookmark. This is because I plan to do it with one
button whose text will update based on the torrent status. Currently it's listed in the settings menu as
"Toggle Bookmark". I think later I may make it update the group's bookmarked status, or have the group do some
kind of confirmation that it was bookmarked and then update the field accordingly. As it is now, you need to
reload the page for the app to see the change.

I've added a success/fail return value to MySoup.pressLink based on the HTTP status code it gets in response to the action
so HTTP 200 OK returns true, anything else is an error and returns false.

I've added to api.torrents.torrents.Group the ability to set the bookmarked property, which is used to update the Group's field
without having to perform re-load of the entire Group. Unfortunately this method currently is public because it's changed from
api.torrents.torrents.TorrentGroup but I'm contemplating re-structuring some of the API classes to use inheritance, so TorrentGroup will
inherit from Group so it can easily get all its fields. I see why the classes are structured how they are now, due to how the response is
structured and how Gson constructs the objects. But maybe there's something we can do.

The TorrentGroup.add/removeBookmark will only change the field if pressLink went ok, and will return the same value returned
by pressLink so the caller of add/remove can check if it went ok as well.

The text for the bookmark button is updated in onPrepareOptionsMenu, so will check against the bookmarked field each time
the options menu is opened, performing a refresh of the field.

I'm still doing a bit of tweaking so there's some stdout print statements that will be cleaned up when I've finalized the feature

#### For Artists
Option is also enabled in ArtistActivity in the same state it is in TorrentGroupActivity

### Enable Notification toggling for Artists
See the notes about the changes necessary to enable this in the API. The changes have been made and now this feature is enabled.

### HTML Formatting For Text
In the API response in text fields that have HTML code in them the app at the moment just shows
the html? I think. At least in my inbox it does, the forum maybe is formatting correctly? I'm unsure
Anyways, to make use of the html formatting when displaying the text in our textview we can do:
```Java
myTextView.setText(Html.fromHtml("some html formatted string"));
```

## Overly Ambitious Fool Plans
Re-design of the app. More focus on fragments instead of so many separate activities
Try follow the Android design guidelines and get something nice and quick.


## Resolved Bugs
#### Japanese/Non-English characters
##### Resolved:
This issue was resolved by adding the apache commons lang3 library to the WhatAPI and using
StringEscapeUtils.unescapeHtml4 in MyStringDeserializer in the API. This bug resolution note will also
be added to the api.

##### Issue:
Seems to not display them correctly instead showing gibberish symbols. Perhaps we aren't drawing
the text with the right encoding? Or reading it with the wrong encoding? This happens on my phone too
so I'm sure I didn't break anything with this issue.

I made some requests to artists with Japanese characters and have found the issue. The response we receive
contains bad characters for the artist name and album names. Basically anywhere that the Kanji characters appear,
I think it's safe to assume this issue would also come up for other special characters but will have to find
an artist with the characters to test on. For now see response for artist id 781409 name 神聖かまってちゃん

where I've edited it to show the relevant areas.
```json
{"status":"success","response":{"id":781409,"name":null,"notificationsEnabled":false,
"hasBookmarked":false,"image":null
```

it does also have an image, so why do we get null here? Note the name being null.
For the torrent groups: album titles should be 8月32日へ for first album and つまんね for second. Instead we get

```json
First:
"torrentgroup":[{"groupId":72287531,"groupName":"8&#26376;32&#26085;&#12408;","groupYear":2011,

Second:
{"groupId":72289938,"groupName":"&#12388;&#12414;&#12435;&#12397;","groupYear":2010,
```

Where now we get garbage for the group names. I tested this in my browser so I think this is a serverside issue?
I'll have to ask Gwindow.

The plot thickens: When I google
```txt
8&#26376;32&#26085;&#12408;
```
it brings up results for 8月32日へ. This is clearly an encoding issue, but what is the encoding being used?
It also seems if I type it in the text here github interprets it correctly.

Have found the cause. The encoding that is being sent in the API response for these characters is HTML Entity (decimal)
but the encoding Java expects is Unicode. We should get something like
```Java
"\u795E"
```
From the API response to receive a unicode character. That or we'll need to do some sort of conversion. I think a tweak
in the API response is probably best though, as this may be an issue for other people using the API too. Although they should
probably be notified of the change if it does change.

#### Trailing spaces in search tags result in a crash
##### Resolved:
Issue is resolved by removing all spaces from the tag string.

##### Issue:
When typing on a device that supports completion often upon completing a word it will insert a trailing space, at least on my
phone Swift Key 3 does. This trailing space when used in a search as a tag will result in an illegal argument exception to creating
the HttpGet as the space is an illegal character.
