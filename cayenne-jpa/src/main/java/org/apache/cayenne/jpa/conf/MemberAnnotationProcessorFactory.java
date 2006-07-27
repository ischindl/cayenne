/*****************************************************************
 *   Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 ****************************************************************/

package org.apache.cayenne.jpa.conf;

import java.lang.reflect.AnnotatedElement;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Temporal;

import org.apache.cayenne.jpa.map.JpaAttributeOverride;
import org.apache.cayenne.jpa.map.JpaAttributes;
import org.apache.cayenne.jpa.map.JpaBasic;
import org.apache.cayenne.jpa.map.JpaColumn;
import org.apache.cayenne.jpa.map.JpaEmbeddable;
import org.apache.cayenne.jpa.map.JpaEmbeddableAttribute;
import org.apache.cayenne.jpa.map.JpaEmbeddedId;
import org.apache.cayenne.jpa.map.JpaEntity;
import org.apache.cayenne.jpa.map.JpaGeneratedValue;
import org.apache.cayenne.jpa.map.JpaId;

/**
 * A factory of member annotation processors.
 * 
 * @author Andrus Adamchik
 */
class MemberAnnotationProcessorFactory extends AnnotationProcessorFactory {

    static final class FlushModeProcessor implements AnnotationProcessor {

        public void onStartElement(
                AnnotatedElement element,
                AnnotationProcessorStack context) {

            // TODO: andrus, 4/23/2006 - where does this annotation belong??

            context.recordConflict(
                    element,
                    EmbeddedId.class,
                    "FlushMode annotation is not fully defined in the specification");
        }

        public void onFinishElement(
                AnnotatedElement element,
                AnnotationProcessorStack context) {
        }
    }

    static final class EmbeddedIdProcessor implements AnnotationProcessor {

        public void onStartElement(
                AnnotatedElement element,
                AnnotationProcessorStack context) {

            JpaEmbeddedId id = new JpaEmbeddedId();

            Object parent = context.peek();
            if (parent instanceof JpaEntity) {
                ((JpaEntity) parent).getAttributes().setEmbeddedId(id);
            }
            else {
                context.recordConflict(element, AnnotationProcessorFactory
                        .annotationClass(getClass()), "Unsupported in this place");
            }

            context.push(id);
        }

        public void onFinishElement(
                AnnotatedElement element,
                AnnotationProcessorStack context) {

            context.pop();
        }
    }

    static final class IdProcessor implements AnnotationProcessor {

        public void onStartElement(
                AnnotatedElement element,
                AnnotationProcessorStack context) {

            JpaId id = new JpaId();

            Object parent = context.peek();
            if (parent instanceof JpaEntity) {
                ((JpaEntity) parent).getAttributes().getIds().add(id);
            }
            else {
                context.recordConflict(element, AnnotationProcessorFactory
                        .annotationClass(getClass()), "Unsupported in this place");
            }

            context.push(id);
        }

        public void onFinishElement(
                AnnotatedElement element,
                AnnotationProcessorStack context) {

            context.pop();
        }
    }

    abstract static class AbstractChildProcessor implements AnnotationProcessor {

        public void onStartElement(
                AnnotatedElement element,
                AnnotationProcessorStack context) {

            Object parent = context.peek();

            if (parent instanceof JpaId) {
                onId((JpaId) parent, element, context);
            }
            else if (parent instanceof JpaEmbeddedId) {
                onEmbeddedId((JpaEmbeddedId) parent, element, context);
            }
            else if (parent instanceof JpaEmbeddableAttribute) {
                onEmbeddableAttribute((JpaEmbeddableAttribute) parent, element, context);
            }
            else if (parent instanceof JpaEmbeddable) {
                JpaEmbeddable embeddable = (JpaEmbeddable) parent;

                // embeddable attribute implied...
                JpaEmbeddableAttribute attribute = new JpaEmbeddableAttribute();
                embeddable.getEmbeddableAttributes().add(attribute);
                context.push(attribute);

                onEmbeddableAttribute(attribute, element, context);
            }
        }

        public void onFinishElement(
                AnnotatedElement element,
                AnnotationProcessorStack context) {

            Object stackTop = context.peek();
            if (stackTop instanceof JpaAttributes) {
                context.pop();
            }
            else if (stackTop instanceof JpaEmbeddableAttribute) {
                context.pop();
            }
        }

        void onEmbeddedId(
                JpaEmbeddedId id,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            recordUnsupportedAnnotation(element, context);
        }

        void onId(JpaId id, AnnotatedElement element, AnnotationProcessorStack context) {
            recordUnsupportedAnnotation(element, context);
        }

        void onEmbeddableAttribute(
                JpaEmbeddableAttribute attribute,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            recordUnsupportedAnnotation(element, context);
        }

        void recordUnsupportedAnnotation(
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            context.recordConflict(element, AnnotationProcessorFactory
                    .annotationClass(getClass()), "Unsupported in this place");
        }
    }

    // ====== Concrete processor classes ========

    static final class AttributeOverrideProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            AttributeOverride annotation = element.getAnnotation(AttributeOverride.class);
//            attribute.getAttributeOverrides().add(new JpaAttributeOverride(annotation));
//        }

        @Override
        void onEmbeddedId(
                JpaEmbeddedId id,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            AttributeOverride annotation = element.getAnnotation(AttributeOverride.class);
            id.getAttributeOverrides().add(new JpaAttributeOverride(annotation));
        }
    }

    static final class AttributeOverridesProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            AttributeOverrides annotation = element
//                    .getAnnotation(AttributeOverrides.class);
//
//            for (int i = 0; i < annotation.value().length; i++) {
//                attribute.getAttributeOverrides().add(
//                        new JpaAttributeOverride(annotation.value()[i]));
//            }
//        }

        @Override
        void onEmbeddedId(
                JpaEmbeddedId id,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            AttributeOverrides annotation = element
                    .getAnnotation(AttributeOverrides.class);

            for (int i = 0; i < annotation.value().length; i++) {
                id.getAttributeOverrides().add(
                        new JpaAttributeOverride(annotation.value()[i]));
            }
        }
    }

    static final class BasicProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            attribute.setBasic(new JpaBasic(element.getAnnotation(Basic.class)));
//        }

        @Override
        void onEmbeddableAttribute(
                JpaEmbeddableAttribute attribute,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            attribute.setBasic(new JpaBasic(element.getAnnotation(Basic.class)));
        }
    }

    static final class ColumnProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            Column annotation = element.getAnnotation(Column.class);
//            attribute.setColumn(new JpaColumn(annotation));
//        }

        @Override
        void onEmbeddableAttribute(
                JpaEmbeddableAttribute attribute,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            Column annotation = element.getAnnotation(Column.class);
            attribute.setColumn(new JpaColumn(annotation));
        }

        @Override
        void onId(JpaId id, AnnotatedElement element, AnnotationProcessorStack context) {
            Column annotation = element.getAnnotation(Column.class);
            id.setColumn(new JpaColumn(annotation));
        }
    }

    static final class EmbeddedProcessor extends AbstractChildProcessor {
//
//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            attribute.setEmbedded(true);
//        }
    }

    static final class EnumeratedProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            Enumerated annotation = element.getAnnotation(Enumerated.class);
//            attribute.setEnumerated(annotation.value());
//        }

        @Override
        void onEmbeddableAttribute(
                JpaEmbeddableAttribute attribute,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            Enumerated annotation = element.getAnnotation(Enumerated.class);
            attribute.setEnumerated(annotation.value());
        }
    }

    static final class GeneratedValueProcessor extends AbstractChildProcessor {

        @Override
        void onId(JpaId id, AnnotatedElement element, AnnotationProcessorStack context) {
            GeneratedValue annotation = element.getAnnotation(GeneratedValue.class);
            id.setGeneratedValue(new JpaGeneratedValue(annotation));
        }
    }

    static final class JoinColumnProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            JoinColumn annotation = element.getAnnotation(JoinColumn.class);
//            attribute.getJoinColumns().add(new JpaJoinColumn(annotation));
//        }
    }

    static final class JoinColumnsProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            JoinColumns annotation = element.getAnnotation(JoinColumns.class);
//
//            for (int i = 0; i < annotation.value().length; i++) {
//                attribute.getJoinColumns().add(new JpaJoinColumn(annotation.value()[i]));
//            }
//        }
    }

    static final class JoinTableProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            JoinTable annotation = element.getAnnotation(JoinTable.class);
//            attribute.setJoinTable(new JpaJoinTable(annotation));
//        }
    }

    static final class LobProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            attribute.setLob(true);
//        }

        @Override
        void onEmbeddableAttribute(
                JpaEmbeddableAttribute attribute,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            attribute.setLob(true);
        }
    }

    static final class ManyToManyProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            ManyToMany annotation = element.getAnnotation(ManyToMany.class);
//            attribute.setManyToMany(new JpaManyToMany(annotation));
//        }
    }

    static final class ManyToOneProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            attribute.setManyToOne(new JpaManyToOne(element
//                    .getAnnotation(ManyToOne.class)));
//        }
    }

    static final class MapKeyProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            MapKey annotation = element.getAnnotation(MapKey.class);
//            attribute.setMapKey(annotation.name());
//        }
    }

    static final class OneToManyProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            attribute.setOneToMany(new JpaOneToMany(element
//                    .getAnnotation(OneToMany.class)));
//        }
    }

    static final class OneToOneProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            attribute.setOneToOne(new JpaOneToOne(element.getAnnotation(OneToOne.class)));
//        }
    }

    static final class OrderByProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            OrderBy annotation = element.getAnnotation(OrderBy.class);
//            attribute.setOrderBy(annotation.value());
//        }
    }

    static final class TemporalProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            Temporal annotation = element.getAnnotation(Temporal.class);
//            attribute.setTemporal(annotation.value());
//        }

        @Override
        void onEmbeddableAttribute(
                JpaEmbeddableAttribute attribute,
                AnnotatedElement element,
                AnnotationProcessorStack context) {
            Temporal annotation = element.getAnnotation(Temporal.class);
            attribute.setTemporal(annotation.value());
        }

        @Override
        void onId(JpaId id, AnnotatedElement element, AnnotationProcessorStack context) {
            Temporal annotation = element.getAnnotation(Temporal.class);
            id.setTemporal(annotation.value());
        }
    }

    static final class TransientProcessor extends AbstractChildProcessor {

//        @Override
//        void onAttribute(
//                JpaAttribute attribute,
//                AnnotatedElement element,
//                AnnotationProcessorStack context) {
//            attribute.setTransient(true);
//        }
    }

    static final class VersionProcessor extends AbstractChildProcessor {

        // @Override
        // void onAttribute(
        // JpaAttribute attribute,
        // AnnotatedElement element,
        // AnnotationProcessorStack context) {
        // attribute.setVersion(true);
        //        }
    }
}
