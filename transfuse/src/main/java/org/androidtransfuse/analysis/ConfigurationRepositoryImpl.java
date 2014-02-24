/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidtransfuse.analysis;

import org.androidtransfuse.ComponentBuilder;
import org.androidtransfuse.ConfigurationRepository;
import org.androidtransfuse.EventMapping;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.util.matcher.Matcher;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author John Ericksen
 */
@Singleton
public class ConfigurationRepositoryImpl implements ConfigurationRepository{

    private final ASTClassFactory astClassFactory;
    private final Map<Matcher<AnnotatedType>, List<EventMapping>> events = new HashMap<Matcher<AnnotatedType>, List<EventMapping>>();

    @Inject
    public ConfigurationRepositoryImpl(ASTClassFactory astClassFactory) {
        this.astClassFactory = astClassFactory;
    }

    public List<EventMapping> getEvents(ASTType type, Class<? extends Annotation> annotation){
        List<EventMapping> matched = new ArrayList<EventMapping>();
        AnnotatedType signature = new AnnotatedType(type, astClassFactory.getType(annotation));

        for (Map.Entry<Matcher<AnnotatedType>, List<EventMapping>> eventEntry : events.entrySet()) {
            if(eventEntry.getKey().matches(signature)){
                matched.addAll(eventEntry.getValue());
            }
        }

        return matched;
    }

    @Override
    public ComponentBuilder component(Class<? extends Annotation> annotation) {
        return new ComponentBuilder(this, annotation);
    }

    @Override
    public void addEvent(Class<? extends Annotation> componentType, String type, EventMapping eventMapping) {
        ASTType matchType = null;
        if(type != null){
            matchType = new ASTStringType(type);
        }
        AnnotatedTypeMatcher annotatedTypeMatcher = new AnnotatedTypeMatcher(matchType, astClassFactory.getType(componentType));

        if(!events.containsKey(annotatedTypeMatcher)){
            events.put(annotatedTypeMatcher, new ArrayList<EventMapping>());
        }
        events.get(annotatedTypeMatcher).add(eventMapping);
    }
}
