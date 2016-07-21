//扩展dateTimeBox 
$.extend($.fn.datagrid.defaults.editors, { 
    datetimebox : { 
        init: function(container, options){ 
             var input = $('<input type="text">').appendTo(container); 
             options.editable = false; 
             input.datetimebox(options); 
             return input; 
        } ,
        getValue: function(target){ 
             return $(target).datetimebox('getValue'); 
        } ,
        setValue: function(target, value){ 
             $(target).datetimebox('setValue', value); 
        } ,
        resize: function(target ,width){ 
             $(target).datetimebox('resize', width); 
        } ,
        destroy : function (target) { 
             $(target).datetimebox('destroy'); 
        } 
    } 
}); 




