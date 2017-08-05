package org.icememo.api

import org.icememo.Result
import org.icememo.entity.Series
import org.icememo.model.SeasonStatModel
import org.icememo.model.post.PostSeries
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("series")
class SeriesController extends BaseController {
    @Autowired
    SeasonController seasonController;

    class SeriesStatModel {
        String name
        List<SeasonStatModel> seasonsStat = []
    }

    @RequestMapping(value = "get-stat", method = RequestMethod.GET)
    @ResponseBody
    public SeriesStatModel getStat(Integer seriesId) {
        def model = new SeriesStatModel()
        Series series = seriesDao.get(seriesId)
        model.name = series.name

        seasonDao.getForSeries(seriesId).forEach({ season ->
            model.seasonsStat << seasonController.getStat(season.id)
        })

        return model
    }

    class AllSeriesStatModel {
        List<SeriesStatModel> seriesArray = []
    }

    @RequestMapping(value = "get-all-stat", method = RequestMethod.GET)
    @ResponseBody
    public AllSeriesStatModel getAllStat() {
        def model = new AllSeriesStatModel()
        seriesDao.all.forEach({ series ->
            model.seriesArray << getStat(series.id)
        })

        return model
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<Series> add(@RequestBody PostSeries postSeries) {
        Series obj = new Series(user: data.user.id);

        obj.name = postSeries.name
        seriesDao.save(obj)

        return new Result<Series>(data: obj)
    }

    @RequestMapping(value = "get-all", method = RequestMethod.GET)
    @ResponseBody
    public List<Series> getAll() {
        return seriesDao.all
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    public Result delete(Integer id) {
        Series series = seriesDao.get(id)
        seriesDao.delete(series)
        return Result.getOk()
    }

}