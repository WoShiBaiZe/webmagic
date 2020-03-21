package com.baize.test;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

public class JobProcessor implements PageProcessor {
    //解析页面
    public void process(Page page) {
        //解析返回的数据page，并且把解析的结果放到ResultItems中d
        page.putField("div",page.getHtml().css("div.box_h").all());

        //XPath
        page.putField("div2",page.getHtml().xpath("//div[@id=news_div]/ul/li/div/a"));

        //正则表达式
        page.putField("div3",page.getHtml().css("div#news_div a").regex("。*江苏。*").all());

        //处理结果API
        page.putField("div4",page.getHtml().css("div#news_div a").regex(".*江苏.*").get());
        page.putField("div5",page.getHtml().css("div#news_div a").regex(".*江苏.*").toString());

        //获取链接
//        page.addTargetRequests(page.getHtml().css("div#J_channels").links().all());
//        page.putField("url",page.getHtml().css("div.floor-title ").all());

        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
        page.addTargetRequest("https://www.jd.com/news.html?id=37319");
    }

    private Site site = Site.me()
            .setCharset("utf8") //设置编码
            .setTimeOut(1000) //设置超时时间，单位ms毫秒
            .setRetrySleepTime(1000) //设置重试的间隔时间 单位ms毫秒
            .setRetryTimes(3) //设置重试次数
            ;
    public Site getSite() {
        return site;
    }

    //主函数直行爬虫
    public static void main(String[] args) {
        Spider spider = Spider.create(new JobProcessor())
                .addUrl("https://www.jd.com/") //设置要爬取数据的页面
                .addPipeline(new FilePipeline("C:\\Users\\BaiZe\\Desktop\\result"))
                .thread(5)//设置有5个线程处理
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000))); //设置去重

        Scheduler scheduler = spider.getScheduler();
        //执行爬虫
        spider.run();
    }
}
