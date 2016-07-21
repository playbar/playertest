#include "my_def.h"
#include <stdio.h>


static int my_out_width = 0;
static int my_out_height = 0;



void my_set_out_size(int out_width, int out_height)
{
    my_out_width = out_width;
    my_out_height = out_height;
}


int my_calc_crop_size(int in_width, int in_height, int* crop_width, int* crop_height)
{
    if (in_width <= 0 || in_height <= 0)
    {
        return -1;
    }
    
    if (my_out_width <=0 || my_out_height <= 0)
    {
        *crop_width = in_width;
        *crop_height = in_height;
        
        return 0;
    }
    
    
    float in_aspect = (float)in_width / (float)in_height;
    float out_aspect = (float)my_out_width / (float)my_out_height;
    
    if (in_aspect == out_aspect)
    {
        *crop_width = in_width;
        *crop_height = in_height;
    }
    else if (in_aspect < out_aspect)
    {
        *crop_width = in_width;
        *crop_height = (float)in_width / out_aspect;
    }
    else
    {
        *crop_width = (float)in_height * out_aspect;
        *crop_height = in_height;
    }
    
    
    return 0;
}
