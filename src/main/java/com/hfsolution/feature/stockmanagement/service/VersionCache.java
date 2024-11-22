// package com.hfsolution.feature.stockmanagement.service;

// import org.springframework.beans.factory.annotation.Value;

// import com.hfsolution.app.dao.BaseCacheDao;

// @Service
// public class VersionCache extends BaseCacheDao {
//     private @Autowired VersionDao versionDao;

//     public VersionCache(@Value("${cache.version.name}") String hasKeyName, @Value("${cache.version.ttl}") long timeToLive) {
//         super(hasKeyName, timeToLive);
//     }

//     public HashMap<String, String> findVersionCache(String para) {
//         String KEY = CACHE_KEY_VERSION;
//         HashMap<String, String> response = new HashMap<>();

//         try {

//             List<Version> versions = new ArrayList<>();
//             VersionCacheDto versionCacheDto = new VersionCacheDto();
//             String versionCache = this.getCacheById(KEY);

//             if (versionCache == null || versionCache.isEmpty()) {
//                 var versionsEntity = versionDao.findByIsShowOnApp(1);

//                 if (!versionsEntity.getStatus().equals(SUCCESS))
//                     throw new DatabaseClientException("NO VERSION PRESENT");

//                 versions.addAll(versionsEntity.getEntityList());
//                 versionCacheDto.setVersions(versions);
//                 versionCacheDto.setId(KEY);
//                 versionCacheDto.setTimeToLiveAsSecond(this.timeToLiveAsSecond);
//                 this.saveCacheAsync(KEY, JsonUtil.convertToJson(versionCacheDto), this.timeToLiveAsSecond);

//             } else {
//                 versionCacheDto = JsonUtil.parseJson(versionCache, VersionCacheDto.class);
//                 versions.addAll(versionCacheDto.getVersions());
//             }

//             response = findVersion(para, versions);

//         } catch (DatabaseClientException e) {
//             throw e;

//         } catch (Exception e) {
//             response = findVersion(para, versionDao.findAll().getEntityList());
//         }

//         return response;
//     }


// }