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
        if (toIndex >= pageCount()) { //当用户浏览到底部时才检查是否有新数据
            "toIndex >= pageCount, toIndex=$toIndex, pageCount=${pageCount()}".logi(RECOMMEND_TAG)
            val newRecommend = fetchNew()
            "newRecommend=${newRecommend.map { it.id }}".logi(RECOMMEND_TAG)
            _recommendList = diff(recommendList, newRecommend)
            "after diff=${recommendList.map { it.id }}".logi(RECOMMEND_TAG)

            if (!hasAddOtherRecommendSource && toIndex >= pageCount()) {
                // 当推荐列表浏览完时，添加其他文章
                hasAddOtherRecommendSource = true
                _recommendList = appendOtherSource(recommendList)
                "after append other source: ${recommendList.map { it.id }}".logi(RECOMMEND_TAG)
            }
            /**
             * 兜底方案，返回数据库中的文章
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
    //是否有删除
    val oldIds = oldArticles.map { it.id }
    val newIds = newArticles.map { it.id }
    if (oldArticles.size == newArticles.size && oldArticles.all { it.id in newIds }) {
        return newArticles
    }
    //TODO 当浏览期间有文章被删除
    val deletedArticles = oldArticles.filter { it.id !in newIds }
    "deletedArticles=$deletedArticles".logi(RECOMMEND_TAG)
    //新增推荐文章
    val newAdditionRecommends = newArticles.filter { it.id !in oldIds }
    "newAdditionRecommends=$newAdditionRecommends".logi(RECOMMEND_TAG)
    val result = mutableListOf<Article>()
    result += oldArticles
    //添加到末尾
    result += newAdditionRecommends
    return result
}