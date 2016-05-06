# StockHawk

Added enhancements to one app in order to make it production ready. The work included ensuring errors were handled gracefully, building a widget for the home screen, adding support for screen readers, optimizations for localization, and data visualization via a library.

###Accessibility and localization 

-   Added content descriptions for screen readers
 1. Set content description dynamically
    
    > fab.setContentDescription(getString(R.string.fab_content_desc));
 2. Set content description through xml.
    
    > android:contentDescription="@string/try_again_content"


-  In AndroidManifest.xml set supportRtl to true , as a result reading becomes easy for user who read screen from right to left. 
   
   >android:supportsRtl="true"




##Android Developer Nanodegree
![Alt text] (https://cloud.githubusercontent.com/assets/7153301/14585363/204b1ee6-048f-11e6-9130-d46d777c4fe7.png)


