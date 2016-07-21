#ifndef __LAYERMANAGER_H__
#define __LAYERMANAGER_H__

#include "set"
#include "string"
#include "cocos2d.h"

class LayerManager{
public:
	enum EnLayerType{
		LAYER_TYPE_SKELETON = 1,
		LAYER_TYPE_PARTICLE = 2,
		LAYER_TYPE_SPRITE3D =3
	};
	static int mzOrder;

	static LayerManager *getInstance();
	void init();

public:

	bool addLayer(std::string strName, EnLayerType type );
	bool delLayer(std::string strName);

private:
	LayerManager();
	virtual ~LayerManager();




};


#endif

