#ifndef __SKELETONLAYER_H__
#define __SKELETONLAYER_H__

#include "cocos2d.h"
#include "spine/spine-cocos2dx.h"

class SkeletonLayer : public cocos2d::Layer
{
    
public:
    void InitSkeleton(const std::string& skeletonDataFile, const std::string& atlasFile, float scale);
    void InitSkeleton(const std::string& name);
    
    void TestInitJson(const std::string& name);
    
    virtual void update(float delta);

    void restartAnimation();
private:
	spine::SkeletonAnimation* skeletonNode;
    spine::SkeletonAnimation* mpSkeletonTmp;
    int miAnimaitonIndex;
    std::string mstrAnimationName;
    bool mbAnimaiton;
public:
    SkeletonLayer();
    virtual bool init();
    bool mbUpdatepos;
    
    // a selector callback
    void menuCloseCallback(cocos2d::Ref* pSender);
    
    // implement the "static create()" method manually
    CREATE_FUNC(SkeletonLayer);
};

#endif // __HELLOWORLD_SCENE_H__

