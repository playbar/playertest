// 取得单例模板对象
template <typename T>
T& CSingle<T>::GetInstance()
{
	static T object;
	return object;
}