var myLayout;

$(document).ready(function () {

  myLayout = $('body').layout({
    //global configuration for all panes
    livePaneResizing:			true
    ,	spacing_open:			0 //panes have 0 width border size
    ,	spacing_closed:			0 

    //NORTH
    ,	north__size:			75
    ,	north__maxSize:			75

    //SOUTH
    ,	south__size:			45
    ,	south__maxSize:			45		

    //WEST
    ,	west__size:			300
    ,	west__spacing_open:		8 //west pane has a border 
    ,	west__spacing_closed:		12
    ,	west__resizable:  		true

    // INNER-LAYOUT
    ,	center__childOptions: {
	center__paneSelector:	".inner-center"
      ,	west__size:		75
      ,	spacing_open:		0  // ALL panes
      ,	spacing_closed:		0  // ALL panes
      ,	west__spacing_closed:	12
      }
  });
});
