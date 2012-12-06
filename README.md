Quiz Application
================

This is a simple quiz application I made for a client.  The rest of the documentation is intended for the client.

Configuration
---------------
You can configure the app by editing `res/values/config.xml`.  The settings are documented in the comments in this file.  The project won't build if there are errors in this file, so you don't have to worry about messing things up too much.

Assets
--------------
Assets live in the project's `assets` directory.  When I say "asset path", I mean a path relative to this directory, for example an asset path `audio/cow-moo.wav` corresponds to the file `$PROJECT_PATH/assets/audio/cow-moo.wav`.

Questions Xml
--------------
The questions live in the asset file given by the config file entry `config_questionsFile`.  The format is pretty straightforward, and the comments in `quiz-example.xml` should make it clear.  The xml is validated while it's being loaded (for example, answers indices are less valid), so you should see errors right away when the app starts.

Styles, colors, and images
---------------------------
These can be configured by editing the following files:
* `res/values/styles.xml` contains some commonly used styles
* `res/values/strings.xml` contains externalized strings. *Most* of the text in the application can be configured here.
* `res/drawable-xhdpi/header_image.png` is the image shown at the top of the categories list.  For production you might want to create smaller images for mdpi, ldpi, and hdpi.
* `res/drawable-*dpi/ic_launcher.png` is the app's icon.

