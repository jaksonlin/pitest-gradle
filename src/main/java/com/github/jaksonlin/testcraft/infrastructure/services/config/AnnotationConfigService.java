package com.github.jaksonlin.testcraft.infrastructure.services.config;

import com.alibaba.fastjson.JSON;
import com.github.jaksonlin.testcraft.domain.annotations.AnnotationSchema;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

@Service(Service.Level.APP)
@State(
        name = "AnnotationConfig",
        storages = {@Storage(value = "$APP_CONFIG$/pitestAnnotationConfig.xml")}
)
public final class AnnotationConfigService implements PersistentStateComponent<AnnotationConfigService.State> {
    private static final Logger LOG = Logger.getInstance(AnnotationConfigService.class);
    public static class State {
        public String schemaJson = AnnotationSchema.getDefaultSchema();
        private AnnotationSchema currentSchema = null;
        public String annotationPackage = "com.example.unittest.annotations";
        public boolean shouldCheckAnnotation = false;
        public boolean autoImport = true;

        public State() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return autoImport == state.autoImport &&
                    Objects.equals(schemaJson, state.schemaJson) &&
                    Objects.equals(annotationPackage, state.annotationPackage);
        }

        @Override
        public int hashCode() {
            return Objects.hash(schemaJson, annotationPackage, autoImport);
        }
    }

    private State myState = new State();

    public static AnnotationConfigService getInstance() {
        return ApplicationManager.getApplication().getService(AnnotationConfigService.class);
    }

    @Override
    public @Nullable State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
        try {
            if (state.schemaJson != null && !state.schemaJson.isEmpty()) {
                myState.currentSchema = JSON.parseObject(state.schemaJson, AnnotationSchema.class);
            }
        } catch (Exception e) {
            LOG.error("Error decoding schema", e);
        }
    }

    public @Nullable AnnotationSchema getSchema() {
        if (myState.currentSchema != null) {
            return getState().currentSchema;
        }
        try {
            if (myState.schemaJson != null && !myState.schemaJson.isEmpty()) {
                myState.currentSchema = JSON.parseObject(myState.schemaJson, AnnotationSchema.class);
            }
            myState.currentSchema = JSON.parseObject(AnnotationSchema.getDefaultSchema(), AnnotationSchema.class);
            return myState.currentSchema;
        } catch (Exception e) {
            try {
                myState.currentSchema = JSON.parseObject(AnnotationSchema.getDefaultSchema(), AnnotationSchema.class);
                return myState.currentSchema;
            } catch (Exception ex) {
                // ignore
                LOG.error("Error decoding default schema", ex);
                return null;
            }
        }
    }

    public void setSchemaJson(String schemaJson) {
        try {
            AnnotationSchema schema = JSON.parseObject(schemaJson, AnnotationSchema.class);
            myState.schemaJson = schemaJson;
            myState.currentSchema = schema;
            LOG.info("Updated annotation config: " + myState.schemaJson);
        } catch (Exception e) {
            LOG.error("Error encoding schema", e);
        }
    }

    public @NotNull AnnotationSchema getDefaultSchema() {
        try {
            return JSON.parseObject(AnnotationSchema.getDefaultSchema(), AnnotationSchema.class);
        } catch (Exception e) {
            LOG.error("Error decoding default schema", e);
            return new AnnotationSchema();
        }
    }

    public String getAnnotationPackage() {
        return myState.annotationPackage;
    }

    public void setAnnotationPackage(String packageName) {
        myState.annotationPackage = packageName;
        LOG.info("Updated annotation package: " + packageName);
    }

    public boolean isAutoImport() {
        return myState.autoImport;
    }

    public void setAutoImport(boolean auto) {
        myState.autoImport = auto;
        LOG.info("Updated auto import setting: " + auto);
    }

    public boolean shouldCheckAnnotation() {
        return myState.shouldCheckAnnotation;
    }

    public void setShouldCheckAnnotation(boolean shouldCheckAnnotation) {
        myState.shouldCheckAnnotation = shouldCheckAnnotation;
        LOG.info("Updated should check annotation setting: " + shouldCheckAnnotation);
    }
}
