#include "NovoScene.h"
#include "SimpleAudioEngine.h"
#include "VisibleRect.h"
#include "spine/spine.h"
#include "base/ccMacros.h"

USING_NS_CC;
using namespace spine;

Scene* NovoLayer::createScene()
{
    // 'scene' is an autorelease object
    auto scene = Scene::create();
    return scene;
}

// on "init" you need to initialize your instance
bool NovoLayer::init()
{
    //////////////////////////////
 
	if (!Layer::init()){
		return false;
	}
    
    return true;
}

void NovoLayer::InitSkeleton(const std::string& skeletonDataFile, const std::string& atlasFile, float scale)
{
	skeletonNode = SkeletonAnimation::createWithFile(skeletonDataFile, atlasFile, scale);
	skeletonNode->setScale(0.5f);

	skeletonNode->setStartListener([this](int trackIndex) {
		spTrackEntry* entry = spAnimationState_getCurrent(skeletonNode->getState(), trackIndex);
		const char* animationName = (entry && entry->animation) ? entry->animation->name : 0;
		log("%d start: %s", trackIndex, animationName);
	});
	skeletonNode->setEndListener([](int trackIndex) {
		log("%d end", trackIndex);
	});
	skeletonNode->setCompleteListener([](int trackIndex, int loopCount) {
		log("%d complete: %d", trackIndex, loopCount);
	});
	skeletonNode->setEventListener([](int trackIndex, spEvent* event) {
		log("%d event: %s, %d, %f, %s", trackIndex, event->data->name, event->intValue, event->floatValue, event->stringValue);
	});

	skeletonNode->setMix("walk", "jump", 0.2f);
	skeletonNode->setMix("jump", "run", 0.2f);
	skeletonNode->setAnimation(0, "animation", true);
	spTrackEntry* jumpEntry = skeletonNode->addAnimation(0, "jump", false, 3);
	skeletonNode->addAnimation(0, "run", true);

    // bigman
    // PlayInfo *pInfo = PlayInfo::getInstance();
	// skeletonNode->setPosition(Vec2(pInfo->getposx(), pInfo->getposy()));

	addChild(skeletonNode, 2);
	scheduleUpdate();

	EventListenerTouchOneByOne* listener = EventListenerTouchOneByOne::create();
	listener->onTouchBegan = [this](Touch* touch, Event* event) -> bool {
		if (!skeletonNode->getDebugBonesEnabled())
			skeletonNode->setDebugBonesEnabled(true);
		else if (skeletonNode->getTimeScale() == 1)
			skeletonNode->setTimeScale(0.3f);
		else
		{
			skeletonNode->setTimeScale(1);
			skeletonNode->setDebugBonesEnabled(false);
		}

		return true;
	};
	_eventDispatcher->addEventListenerWithSceneGraphPriority(listener, this);

}


void NovoLayer::InitShapSkeleton()
{
	skeletonShip = SkeletonAnimation::createWithFile("spine/powerup.json", "spine/powerup.atlas", 0.6f);
	//skeletonShip = SkeletonAnimation::createWithFile("D:/test/spine/ship.json", "D:/test/spine/ship.atlas", 0.6f);
	skeletonShip->setScale(1);

	skeletonShip->setStartListener([this](int trackIndex) {
		spTrackEntry* entry = spAnimationState_getCurrent(skeletonShip->getState(), trackIndex);
		const char* animationName = (entry && entry->animation) ? entry->animation->name : 0;
		log("%d start: %s", trackIndex, animationName);
	});
	skeletonShip->setEndListener([](int trackIndex) {
		log("%d end", trackIndex);
	});
	skeletonShip->setCompleteListener([](int trackIndex, int loopCount) {
		log("%d complete: %d", trackIndex, loopCount);
	});
	skeletonShip->setEventListener([](int trackIndex, spEvent* event) {
		log("%d event: %s, %d, %f, %s", trackIndex, event->data->name, event->intValue, event->floatValue, event->stringValue);
	});

	skeletonShip->setMix("walk", "jump", 0.2f);
	skeletonShip->setMix("jump", "run", 0.2f);
	skeletonShip->setAnimation(0, "animation", true);
	spTrackEntry* jumpEntry = skeletonShip->addAnimation(0, "jump", false, 3);
	skeletonShip->addAnimation(0, "run", true);

	Size windowSize = Director::getInstance()->getWinSize();
	skeletonShip->setPosition(Vec2(windowSize.width / 2, 40));

	addChild(skeletonShip, 2);
	scheduleUpdate();

	EventListenerTouchOneByOne* listener = EventListenerTouchOneByOne::create();
	listener->onTouchBegan = [this](Touch* touch, Event* event) -> bool {
		if (!skeletonShip->getDebugBonesEnabled())
			skeletonShip->setDebugBonesEnabled(true);
		else if (skeletonShip->getTimeScale() == 1)
			skeletonShip->setTimeScale(0.3f);
		else
		{
			skeletonShip->setTimeScale(1);
			skeletonShip->setDebugBonesEnabled(false);
		}

		return true;
	};
	_eventDispatcher->addEventListenerWithSceneGraphPriority(listener, this);

}

void NovoLayer::TestParticle()
{
	_emitter = ParticleSystemQuad::create("Particles/SmallSun.plist");
	_emitter->setPosition(400, 200);
	_emitter->retain();
	addChild(_emitter, 10);
}

void NovoLayer::initSprite3D()
{
	auto s = Director::getInstance()->getWinSize();
	addNewSpriteWithCoords(Vec2(s.width / 2, s.height / 2));

	auto listener = EventListenerTouchAllAtOnce::create();
	listener->onTouchesEnded = CC_CALLBACK_2(NovoLayer::onTouchesEnded, this);
	_eventDispatcher->addEventListenerWithSceneGraphPriority(listener, this);
	TTFConfig ttfConfig("fonts/arial.ttf", 20);
	auto label1 = Label::createWithTTF(ttfConfig, "Hair");
	auto item1 = MenuItemLabel::create(label1, CC_CALLBACK_1(NovoLayer::menuCallback_reSkin, this));
	auto label2 = Label::createWithTTF(ttfConfig, "Glasses");
	auto item2 = MenuItemLabel::create(label2, CC_CALLBACK_1(NovoLayer::menuCallback_reSkin, this));
	auto label3 = Label::createWithTTF(ttfConfig, "Coat");
	auto item3 = MenuItemLabel::create(label3, CC_CALLBACK_1(NovoLayer::menuCallback_reSkin, this));
	auto label4 = Label::createWithTTF(ttfConfig, "Pants");
	auto item4 = MenuItemLabel::create(label4, CC_CALLBACK_1(NovoLayer::menuCallback_reSkin, this));
	auto label5 = Label::createWithTTF(ttfConfig, "Shoes");
	auto item5 = MenuItemLabel::create(label5, CC_CALLBACK_1(NovoLayer::menuCallback_reSkin, this));
	item1->setPosition(Vec2(VisibleRect::left().x + 50, VisibleRect::bottom().y + item1->getContentSize().height * 4));
	item2->setPosition(Vec2(VisibleRect::left().x + 50, VisibleRect::bottom().y + item1->getContentSize().height * 5));
	item3->setPosition(Vec2(VisibleRect::left().x + 50, VisibleRect::bottom().y + item1->getContentSize().height * 6));
	item4->setPosition(Vec2(VisibleRect::left().x + 50, VisibleRect::bottom().y + item1->getContentSize().height * 7));
	item5->setPosition(Vec2(VisibleRect::left().x + 50, VisibleRect::bottom().y + item1->getContentSize().height * 8));
	item1->setUserData((void*)SkinType::HAIR);
	item2->setUserData((void*)SkinType::GLASSES);
	item3->setUserData((void*)SkinType::UPPER_BODY);
	item4->setUserData((void*)SkinType::PANTS);
	item5->setUserData((void*)SkinType::SHOES);
	auto pMenu1 = Menu::create(item1, item2, item3, item4, item5, nullptr);
	pMenu1->setPosition(Vec2(0, 0));
	this->addChild(pMenu1, 3);
}

void NovoLayer::onTouchesEnded(const std::vector<cocos2d::Touch*>& touches, cocos2d::Event* event)
{

}

void NovoLayer::addNewSpriteWithCoords(cocos2d::Vec2 p)
{
	std::string fileName = "Sprite3DTest/ReskinGirl.c3b";
	auto sprite = Sprite3D::create(fileName);
	sprite->setScale(6);
	sprite->setRotation3D(Vec3(0, 0, 0));
	addChild(sprite, 1);
	sprite->setPosition(Vec2(p.x, p.y - 100));
	auto animation = Animation3D::create(fileName);
	if (animation)
	{
		auto animate = Animate3D::create(animation);

		sprite->runAction(RepeatForever::create(animate));
	}
	_sprite = sprite;

	auto& body = _skins[(int)SkinType::UPPER_BODY];
	body.push_back("Girl_UpperBody01");
	body.push_back("Girl_UpperBody02");

	auto& pants = _skins[(int)SkinType::PANTS];
	pants.push_back("Girl_LowerBody01");
	pants.push_back("Girl_LowerBody02");

	auto& shoes = _skins[(int)SkinType::SHOES];
	shoes.push_back("Girl_Shoes01");
	shoes.push_back("Girl_Shoes02");

	auto& hair = _skins[(int)SkinType::HAIR];
	hair.push_back("Girl_Hair01");
	hair.push_back("Girl_Hair02");

	auto& face = _skins[(int)SkinType::FACE];
	face.push_back("Girl_Face01");
	face.push_back("Girl_Face02");

	auto& hand = _skins[(int)SkinType::HAND];
	hand.push_back("Girl_Hand01");
	hand.push_back("Girl_Hand02");

	auto& glasses = _skins[(int)SkinType::GLASSES];
	glasses.push_back("");
	glasses.push_back("Girl_Glasses01");

	memset(_curSkin, 0, (int)SkinType::MAX_TYPE * sizeof(int));

	applyCurSkin();
}

void NovoLayer::menuCallback_reSkin(cocos2d::Ref* sender)
{
	long index = (long)(((MenuItemLabel*)sender)->getUserData());
	if (index < (int)SkinType::MAX_TYPE)
	{
		_curSkin[index] = (_curSkin[index] + 1) % _skins[index].size();
		applyCurSkin();
	}
}

void NovoLayer::applyCurSkin()
{
	for (ssize_t i = 0; i < _sprite->getMeshCount(); i++) {
		auto mesh = _sprite->getMeshByIndex(static_cast<int>(i));
		bool isVisible = false;
		for (int j = 0; j < (int)SkinType::MAX_TYPE; j++) {
			if (mesh->getName() == _skins[j].at(_curSkin[j]))
			{
				isVisible = true;
				break;
			}
		}
		_sprite->getMeshByIndex(static_cast<int>(i))->setVisible(isVisible);
	}
}


void NovoLayer::menuCloseCallback(Ref* pSender)
{
    Director::getInstance()->end();

#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif
}
