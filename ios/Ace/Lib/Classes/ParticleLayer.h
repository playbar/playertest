#ifndef __PARTICLELAYER_H__
#define __PARTICLELAYER_H__

#include "cocos2d.h"
#include "spine/spine-cocos2dx.h"

class ParticleLayer : public cocos2d::Layer
{

private:

	cocos2d::ParticleSystemQuad* _emitter;
	void TestParticle();

public:

    virtual bool init();
    
    // a selector callback
    void menuCloseCallback(cocos2d::Ref* pSender);
    
    // implement the "static create()" method manually
    CREATE_FUNC(ParticleLayer);
};

#endif // __HELLOWORLD_SCENE_H__
