#include "SkeletonLayer.h"
#include "spine/spine.h"
#include "base/ccMacros.h"
//#include "PlayInfo.h"
#include "document.h"
#include "RNTCWithOCTranslate.h"

USING_NS_CC;
using namespace spine;



// on "init" you need to initialize your instance

SkeletonLayer::SkeletonLayer(){
    mbUpdatepos = false;
    mpSkeletonTmp = nullptr;
    miAnimaitonIndex = 0;
    mstrAnimationName = "";
    mbAnimaiton = false;
}


bool SkeletonLayer::init()
{
    //////////////////////////////
 
	if (!Layer::init()){
		return false;
	}
    
    return true;
}


void SkeletonLayer::InitSkeleton(const std::string& skeletonDataFile, const std::string& atlasFile, float scale)
{
	//skeletonNode = SkeletonAnimation::createWithFile("spine/ship.json", "spine/ship.atlas", 1.0f);
	skeletonNode = SkeletonAnimation::createWithFile(skeletonDataFile, atlasFile, scale);
	skeletonNode->setScale(0.5f);

	skeletonNode->setStartListener([this](int trackIndex) {
		spTrackEntry* entry = spAnimationState_getCurrent(skeletonNode->getState(), trackIndex);
		const char* animationName = (entry && entry->animation) ? entry->animation->name : 0;
		log("%d start: %s", trackIndex, animationName);
	});
    
    skeletonNode->setEndListener([this](int trackIndex){
        int nHandle = 0;
        nHandle++;
        skeletonNode->stopAllActions();
        Director::getInstance()->delLayer(this);
        
    });
    
    skeletonNode->setCompleteListener([this](int trackInex, int loopCount){
        int nHandle = trackInex;
        nHandle++;
//        if( loopCount == 5 ){
//            skeletonNode->stopAllActions();
//            Director::getInstance()->pushDelLayer(this);
//        }
    });
    
	skeletonNode->setEventListener([](int trackIndex, spEvent* event) {
		log("%d event: %s, %d, %f, %s", trackIndex, event->data->name, event->intValue, event->floatValue, event->stringValue);
	});

	skeletonNode->setMix("walk", "jump", 0.2f);
	skeletonNode->setMix("jump", "run", 0.2f);
	skeletonNode->setAnimation(0, "animation", true );
	//skeletonNode->addAnimation(0, "jump", false, 3);
	//skeletonNode->addAnimation(0, "run", false);

    //PlayInfo *pInfo = PlayInfo::getInstance();
	skeletonNode->setPosition(0, 0);

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

void SkeletonLayer::InitSkeleton(const std::string& name){
    
    Data data = FileUtils::getInstance()->getDataFromFile( FileUtils::getInstance()->fullPathForFilename(name));
    
    using rapidjson::Document;

    Document doc;
    
    std::string load_str((const char* )data.getBytes(), data.getSize());
    
    doc.Parse(RNTCWithOCTranslate::cancelSpace(const_cast<char*>(load_str.c_str())));
    
    //doc.Parse(json);
    if (doc.HasParseError()) {
        rapidjson::ParseErrorCode code = doc.GetParseError();
        log("SkeletonLayer::InitSkeleton error: %d, data:%zd", code, data.getSize() );
        return;
    }

    int pos = name.find_last_of('/');
    std::string dir = name.substr(0, pos + 1);
    
    
    using rapidjson::Value;
    Value & v = doc["ID"];
    if (v.IsString()) {
        log("%s", v.GetString());
    }
    Value &contents = doc["Skeleton"];
    if( contents.IsArray()){
        for( int i = 0; i < contents.Size(); ++i ){
            Value &v = contents[i];
            spine::SkeletonAnimation* skeletonTmp = nullptr;
            if( v.HasMember("file") && v["file"].IsString()){
                std::string jsonFile = dir + v["file"].GetString() + ".json";
                std::string atlasFile = dir + v["file"].GetString() + ".atlas";
                skeletonTmp = SkeletonAnimation::createWithFile(jsonFile, atlasFile, 1.0 );
                skeletonTmp->setEndListener([this](int trackIndex){
                    RNTCWithOCTranslate::CallBackBySkeletonLayer(this->getName());
                });
            }
            else{
                return;
            }
            if (v.HasMember("scale") && v["scale"].IsDouble()) {
                if( skeletonTmp != nullptr ){
                    skeletonTmp->setScale(v["scale"].GetDouble());
                }
            }
            
            if(v.HasMember("posx") && v["posx"].IsDouble()
               &&v.HasMember("posy") && v["posy"].IsDouble()){
                if( skeletonTmp != nullptr ){
                    // 获取屏幕坐标比例
                    double dx = v["posx"].GetDouble();
                    double dy = v["posy"].GetDouble();
                    Size wins = Director::getInstance()->getWinSize();
                    dx = wins.width*dx;
                    dy = wins.height*dy;
                    skeletonTmp->setPosition(dx , dy);
                }
            }
            
       
            if(v.HasMember("loopcount") && v["loopcount"].IsInt()){
                static int iloop = v["loopcount"].GetInt();
                skeletonTmp->setCompleteListener([this](int trackIndex, int loopCount){
                    if(iloop == loopCount ){
                        RNTCWithOCTranslate::CallBackBySkeletonLayer(this->getName());
                    }
                });
                
            }
            Value &mix = v["mix"];
            if( mix.IsArray() ){
                for( int j = 0; j < mix.Size(); ++j){
                    Value &mixValue = mix[j];
                    if( mixValue.HasMember("from") && mixValue["from"].IsString()
                       && mixValue.HasMember("to") && mixValue["to"].IsString()
                       && mixValue.HasMember("duration") && mixValue["duration"].IsDouble()){
                        if( skeletonTmp != nullptr ){
                            log("%s", mixValue["from"].GetString());
                            log("%s", mixValue["to"].GetString());
                            log("%f", mixValue["duration"].GetDouble());
                            skeletonTmp->setMix(mixValue["from"].GetString(), mixValue["to"].GetString(), mixValue["duration"].GetDouble());
                        }
                        
                    }
                   
                }
            }
            Value &addAnimatoin = v["addAnimatoin"];
            if( addAnimatoin.IsArray()){
                for( int x = 0; x < addAnimatoin.Size(); ++x ){
                    Value &valAddAnimatoin = addAnimatoin[x];
                    if( valAddAnimatoin.HasMember("index") && valAddAnimatoin["index"].IsInt()
                       && valAddAnimatoin.HasMember("name") && valAddAnimatoin["name"].IsString()
                       && valAddAnimatoin.HasMember("loop") && valAddAnimatoin["loop"].IsBool()){
                        if( skeletonTmp != nullptr ){
                            
                            mpSkeletonTmp = skeletonTmp;
                            miAnimaitonIndex = valAddAnimatoin["index"].GetInt();
                            mstrAnimationName = valAddAnimatoin["name"].GetString();
                            mbAnimaiton = valAddAnimatoin["loop"].GetBool();
                            skeletonTmp->setAnimation(miAnimaitonIndex, mstrAnimationName,
                                mbAnimaiton);
                        }
                        
                    }
                }
            }
            
            addChild( skeletonTmp);
            scheduleUpdate();
            
        }
    }

    return;
}

void SkeletonLayer::restartAnimation(){
    
    mpSkeletonTmp->setAnimation(miAnimaitonIndex,
                              mstrAnimationName,
                              mbAnimaiton);
}

//#define psln(x) std::cout << #x " = " << (x) << std::endl

void SkeletonLayer::TestInitJson(const std::string& name) {

	using std::string;
	using std::ifstream;

	Data data = FileUtils::getInstance()->getDataFromFile(
				FileUtils::getInstance()->fullPathForFilename(name));

	using rapidjson::Document;
	Document doc;
	doc.Parse((char* )data.getBytes());
    //doc.Parse(json);
    if (doc.HasParseError()) {
        rapidjson::ParseErrorCode code = doc.GetParseError();
        log("%d", code );
        return;
    }
    using rapidjson::Value;
    Value & v = doc["ID"];
    if (v.IsString()) {
        log("%s", v.GetString());
    }
    Value &contents = doc["Skeleton"];
    if( contents.IsArray()){
        for( size_t i = 0; i < contents.Size(); ++i ){
            Value &v = contents[i];
            if( v.HasMember("file") && v["file"].IsString()){
                log("%s", v["file"].GetString());
            }
            if (v.HasMember("scale") && v["scale"].IsDouble()) {
                log("%f", v["scale"].GetDouble());
            }
            if(v.HasMember("posx") && v["posx"].IsInt()){
                log( "%d", v["posx"].GetInt());
            }
            if( v.HasMember("posy") && v["posy"].IsInt()){
                log("%d", v["posy"].GetInt());
            }
            if(v.HasMember("loopcount") && v["loopcount"].IsInt()){
                log("%d", v["loopcount"].GetInt());
            }
            Value &mix = v["mix"];
            if( mix.IsArray() ){
                for( size_t j = 0; j < mix.Size(); ++j){
                    Value &mixValue = mix[j];
                    if( mixValue.HasMember("from") && mixValue["from"].IsString()){
                        log("%s", mixValue["from"].GetString());
                    }
                    if( mixValue.HasMember("to") && mixValue["to"].IsString()){
                        log("%s", mixValue["to"].GetString());
                    }
                    if( mixValue.HasMember("duration") && mixValue["duration"].IsDouble()){
                        log("%f", mixValue["duration"].GetDouble());
                    }
                }
            }
            Value &addAnimatoin = v["addAnimatoin"];
            if( addAnimatoin.IsArray()){
                for( size_t x = 0; x < addAnimatoin.Size(); ++x ){
                    Value &valAddAnimatoin = addAnimatoin[x];
                    if( valAddAnimatoin.HasMember("index") && valAddAnimatoin["index"].IsInt()){
                        log("%d", valAddAnimatoin["index"].GetInt());
                    }
                    if( valAddAnimatoin.HasMember("name") && valAddAnimatoin["name"].IsString()){
                        log("%s", valAddAnimatoin["name"].GetString());
                    }
                    if( valAddAnimatoin.HasMember("loop") && valAddAnimatoin["loop"].IsBool()){
                        log("%d", valAddAnimatoin["loop"].GetBool());
                    }
                }
            }
            
        }
    }
    
    
    return;


//
//	// use values in parse result.
//	using rapidjson::Value;
//	Value & v = doc["dictVersion"];
//	if (v.IsInt()) {
//		psln(v.GetInt());
//	}
//
//	Value & contents = doc["content"];
//	if (contents.IsArray()) {
//		for (size_t i = 0; i < contents.Size(); ++i) {
//			Value & v = contents[i];
//			assert(v.IsObject());
//			if (v.HasMember("key") && v["key"].IsString()) {
//				psln(v["key"].GetString());
//			}
//			if (v.HasMember("value") && v["value"].IsString()) {
//				psln(v["value"].GetString());
//			}
//		}
//	}

}

void SkeletonLayer::update(float delta){
    Layer::update(delta);
    //log("delta: %f", delta );
    if( mbUpdatepos){
        static float posx = -200;
        posx += delta * 100;
        if( posx > 200 )
            posx = -200;
        skeletonNode->setPosition( posx, 0);
    }

}

void SkeletonLayer::menuCloseCallback(Ref* pSender)
{
    Director::getInstance()->end();

#if (CC_TARGET_PLATFORM == CC_PLATFORM_IOS)
    exit(0);
#endif
}
