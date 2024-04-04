package com.example.dao.article.recommend

import com.example.dao.article.ArticleDao
import com.example.dao.article.DefaultArticleDao
import com.example.models.Article
import com.example.util.dbTransaction
import com.example.util.logi

class RecommendArticlesDao(
    private val loginUserId: Long
) : DefaultArticleDao() {
    private var _pageParam: PageParam? = null
    private val pageParam get() = _pageParam!!

    private val otherRecommendSource: List<OtherRecommendSource> = listOf(DurationTop10())

    override suspend fun pages(pageStart: Int, perPageCount: Int): List<Article> = dbTransaction {
        _pageParam = PageParam(pageStart, perPageCount)
        getAllRecommend()
        recommendList.page(pageStart, perPageCount)
    }

    override suspend fun pageCount(): Long {
        return recommendList.size.toLong()
    }

    private var _recommendList: List<Article>? = null
    private val recommendList: List<Article> get() = _recommendList!!
    private suspend fun getAllRecommend() {
        "loginUserId=$loginUserId, create LunimaryPageDao".logi(RECOMMEND_TAG)
        if (_recommendList == null) {
            _recommendList = fetchNew()
        }
        checkUpdate()
    }

    private suspend fun fetchNew(): List<Article> {
        return RecommendArticlesFacade.recommendArticles(loginUserId)
    }

    private var hasAddOtherRecommendSource = false
    private suspend fun checkUpdate() {
        val toIndex = pageOffset(pageParam.pageStart, pageParam.perPageCount) + pageParam.perPageCount
        "toIndex=$toIndex, pageCount=${pageCount()}".logi(RECOMMEND_TAG)
        if (toIndex >= pageCount()) { //���û�������ײ�ʱ�ż���Ƿ���������
            "toIndex >= pageCount, toIndex=$toIndex, pageCount=${pageCount()}".logi(RECOMMEND_TAG)
            val newRecommend = fetchNew()
            "newRecommend=${newRecommend.map { it.id }}".logi(RECOMMEND_TAG)
            _recommendList = diff(recommendList, newRecommend)
            "after diff=${recommendList.map { it.id }}".logi(RECOMMEND_TAG)

            if (!hasAddOtherRecommendSource && toIndex >= pageCount()) {
                // ���Ƽ��б������ʱ�������������
                hasAddOtherRecommendSource = true
                _recommendList = appendOtherSource(recommendList)
                "after append other source: ${recommendList.map { it.id }}".logi(RECOMMEND_TAG)
            }
            /**
             * ���׷������������ݿ��е�����
             */
            if (toIndex >= pageCount()) {
                _recommendList = append(old = recommendList)
                "append: ${recommendList.map { it.id }}".logi(RECOMMEND_TAG)
            }
        }
    }

    private suspend fun appendOtherSource(old: List<Article>): List<Article> {
        val result = mutableListOf<Article>()
        result += old
        val oldIds = old.map { it.id }
        otherRecommendSource.forEach {
            result += it.getArticles(loginUserId, oldIds)
        }
        return result
    }

    private suspend fun append(old: List<Article>): List<Article> {
        val result = mutableListOf<Article>()
        result += old
        result += ArticleDao.getArticles(pageParam.perPageCount * 4, old.map { it.id })
        return result
    }
}

data class PageParam(
    val pageStart: Int,
    val perPageCount: Int
)

/**
 * @param oldArticles
 */
fun diff(oldArticles: List<Article>, newArticles: List<Article>): List<Article> {
    //�Ƿ���ɾ��
    val oldIds = oldArticles.map { it.id }
    val newIds = newArticles.map { it.id }
    if (oldArticles.size == newArticles.size && oldArticles.all { it.id in newIds }) {
        return newArticles
    }
    //TODO ������ڼ������±�ɾ��
    val deletedArticles = oldArticles.filter { it.id !in newIds }
    "deletedArticles=$deletedArticles".logi(RECOMMEND_TAG)
    //�����Ƽ�����
    val newAdditionRecommends = newArticles.filter { it.id !in oldIds }
    "newAdditionRecommends=$newAdditionRecommends".logi(RECOMMEND_TAG)
    val result = mutableListOf<Article>()
    result += oldArticles
    //��ӵ�ĩβ
    result += newAdditionRecommends
    return result
}