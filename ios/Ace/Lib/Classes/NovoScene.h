#ifndef __HELLOWORLD_SCENE_H__
#define __HELLOWORLD_SCENE_H__

#include "cocos2d.h"
#include "spine/spine-cocos2dx.h"

class NovoLayer : public cocos2d::Layer
{
private:
	spine::SkeletonAnimation* skeletonNode;
	spine::SkeletonAnimation* skeletonShip;

	void InitSkeleton(const std::string& skeletonDataFile, const std::string& atlasFile, float scale);
	void InitShapSkeleton();

	cocos2d::ParticleSystemQuad* _emitter;
	void TestParticle();
    
private:
	void initSprite3D();
	void onTouchesEnded(const std::vector<cocos2d::Touch*>& touches, cocos2d::Event* event);
	void addNewSpriteWithCoords(cocos2d::Vec2 p);
	void menuCallback_reSkin(cocos2d::Ref* sender);
	void applyCurSkin();

	enum class SkinType
	{
		UPPER_BODY = 0,
		PANTS,
		SHOES,
		HAIR,
		FACE,
		HAND,
		GLASSES,
		MAX_TYPE,
	};
	std::vector<std::string> _skins[(int)SkinType::MAX_TYPE]; //all skins
	int                      _curSkin[(int)SkinType::MAX_TYPE]; //current skin index
	cocos2d::Sprite3D* _sprite;

public:
    static cocos2d::Scene* createScene();

    virtual bool init();
    
    // a selector callback
    void menuCloseCallback(cocos2d::Ref* pSender);
    
    // implement the "static create()" method manually
    CREATE_FUNC(NovoLayer);
};

#endif // __HELLOWORLD_SCENE_H__
