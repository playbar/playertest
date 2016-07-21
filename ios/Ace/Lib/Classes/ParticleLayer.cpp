#include "ParticleLayer.h"
#include "spine/spine.h"
#include "base/ccMacros.h"
//#include "PlayInfo.h"

USING_NS_CC;
using namespace spine;



// on "init" you need to initialize your instance
bool ParticleLayer::init()
{
    //////////////////////////////
 
	if (!Layer::init()){
		return false;
	}
    
    auto visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();

    _emitter = ParticleSystemQuad::create("Particles/test.plist");
    _emitter->setPosition(400, 200);
    //_emitter->setScale( 0.5);
    _emitter->retain();
    addChild(_emitter, 10);

    return true;
}




void ParticleLayer::TestParticle()
{

}


