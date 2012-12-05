Quiz Application
================

This is a simple quiz application made for a client.  It is intended to be used in multiple released apps, and emphasizes configurability.

Because the configuration and questions are bundled with the app, it needs to be built to change these features.  You will need to install [eclipse](http://www.eclipse.org/downloads/) and the [adt plugin](http://developer.android.com/sdk/installing/installing-adt.html) to do this.

Common Concepts
---------------- 
* A *config resource* is a value specified in the resource file at `res/values/config.xml`.  The format for this file is [documented here](http://developer.android.com/guide/topics/resources/index.html). *Do not remove any of the values from this file*.
* An *asset path* refers to the path of a file placed in the assets project directory, for example, the asset path `"audio/cow-moo.wav"` refers to the project path `"assets/audio/cow-moo.wav"`.
	
Xml Format
----------------
Each released application is bundled with a fixed set of questions, stored as xml in an asset file.  The xml file name is given by the config resource `quizFile` (see below), which defaults to `"quiz.xml"`.  This file is an asset, so it's path is relative to the assets project directory.


