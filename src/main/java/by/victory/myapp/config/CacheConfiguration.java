package by.victory.myapp.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.*;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.cache.PrefixedKeyGenerator;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private GitProperties gitProperties;
    private BuildProperties buildProperties;
    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration =
            Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                    .build()
            );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, by.victory.myapp.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, by.victory.myapp.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, by.victory.myapp.domain.User.class.getName());
            createCache(cm, by.victory.myapp.domain.Authority.class.getName());
            createCache(cm, by.victory.myapp.domain.User.class.getName() + ".authorities");
            createCache(cm, by.victory.myapp.domain.Transport.class.getName());
            createCache(cm, by.victory.myapp.domain.Transport.class.getName() + ".trips");
            createCache(cm, by.victory.myapp.domain.Driver.class.getName());
            createCache(cm, by.victory.myapp.domain.Driver.class.getName() + ".trips");
            createCache(cm, by.victory.myapp.domain.Product.class.getName());
            createCache(cm, by.victory.myapp.domain.Product.class.getName() + ".statements");
            createCache(cm, by.victory.myapp.domain.Trip.class.getName());
            createCache(cm, by.victory.myapp.domain.Trip.class.getName() + ".statements");
            createCache(cm, by.victory.myapp.domain.ImportProd.class.getName());
            createCache(cm, by.victory.myapp.domain.ExportProd.class.getName());
            createCache(cm, by.victory.myapp.domain.Grade.class.getName());
            createCache(cm, by.victory.myapp.domain.Grade.class.getName() + ".importProds");
            createCache(cm, by.victory.myapp.domain.Grade.class.getName() + ".exportProds");
            createCache(cm, by.victory.myapp.domain.Statement.class.getName());
            createCache(cm, by.victory.myapp.domain.StatementType.class.getName());
            createCache(cm, by.victory.myapp.domain.StatementType.class.getName() + ".statements");
            createCache(cm, by.victory.myapp.domain.Positioning.class.getName());
            createCache(cm, by.victory.myapp.domain.Positioning.class.getName() + ".statements");
            createCache(cm, by.victory.myapp.domain.Positioning.class.getName() + ".trips");
            createCache(cm, by.victory.myapp.domain.ProductUnit.class.getName());
            createCache(cm, by.victory.myapp.domain.ProductUnit.class.getName() + ".products");
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
    }
}
