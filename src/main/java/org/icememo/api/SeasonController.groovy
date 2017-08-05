package org.icememo.api

import org.icememo.Result
import org.icememo.dao.FullVersionFunctions
import org.icememo.entity.Season
import org.icememo.model.SeasonStatModel
import org.icememo.model.post.PostSeason
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("season")
class SeasonController extends BaseController{
    @RequestMapping(value = "get", method = RequestMethod.GET)
    @ResponseBody
    public Season getSeason(Integer id) {
        Season season = seasonDao.get(id)
        return season
    }

    @RequestMapping(value = "get-stat", method = RequestMethod.GET)
    @ResponseBody
    public SeasonStatModel getStat(Integer seasonId) {
        return FullVersionFunctions.getStat(this, seasonId)
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public Result<Season> add(@RequestBody PostSeason post) {
        Season season = new Season(user: data.user.id)
        season.name = post.name
        season.series = post.series

        //Volume from last season
        List<Season> seasonList = seasonDao.getForSeries(post.series)
        if(seasonList.size() > 0){
            Season lastSeason = seasonList.get(seasonList.size() - 1)
            season.volume = lastSeason.volume
        }

        seasonDao.save(season)

        return new Result<Season>(data: season)
    }

    @RequestMapping(value = "get-array", method = RequestMethod.GET)
    @ResponseBody
    public List<Season> getArray(Integer seriesId) {
        List<Season> seasons = seasonDao.getForSeries(seriesId);

        return seasons.toSorted { a, b -> a.name <=> b.name }
    }

    @RequestMapping(value = "get-all", method = RequestMethod.GET)
    @ResponseBody
    public List<Season> getAll() {
        return seasonDao.getAll()
    }

    @RequestMapping(value = "update", method = RequestMethod.GET)
    @ResponseBody
    public Result updateSeason(@RequestParam Integer id, String name, Double volume){
        Season season = seasonDao.get(id)
        if(name != null){
            season.name = name
            season.volume = volume
        }
        seasonDao.update(season)

        return Result.getOk()
    }

    @RequestMapping(value = "delete", method = RequestMethod.GET)
    @ResponseBody
    public Result delete(Integer id) {
        Season season = seasonDao.get(id)
        seasonDao.delete(season)
        return Result.getOk()
    }
}
