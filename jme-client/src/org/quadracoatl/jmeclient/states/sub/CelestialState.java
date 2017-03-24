
package org.quadracoatl.jmeclient.states.sub;

import java.util.HashMap;
import java.util.Map;

import org.quadracoatl.environments.ClientEnvironment;
import org.quadracoatl.framework.common.MathUtil;
import org.quadracoatl.framework.common.Vector3d;
import org.quadracoatl.framework.entities.Component;
import org.quadracoatl.framework.entities.Entity;
import org.quadracoatl.framework.entities.EntityContainer;
import org.quadracoatl.framework.entities.changes.ComponentChange;
import org.quadracoatl.framework.entities.changes.EntityChange;
import org.quadracoatl.framework.entities.changes.EntityChangeBatch;
import org.quadracoatl.framework.entities.changes.EntityChangeProcessor;
import org.quadracoatl.framework.entities.components.ColorComponent;
import org.quadracoatl.framework.entities.components.RotationComponent;
import org.quadracoatl.framework.entities.components.SizeComponent;
import org.quadracoatl.framework.entities.components.TextureComponent;
import org.quadracoatl.framework.logging.Logger;
import org.quadracoatl.framework.logging.LoggerFactory;
import org.quadracoatl.framework.realm.components.CelestialObjectComponent;
import org.quadracoatl.framework.realm.components.SkyComponent;
import org.quadracoatl.jmeclient.extensions.JmeResourceManager;
import org.quadracoatl.jmeclient.spatials.celestial.AbstractSkySpatial;
import org.quadracoatl.jmeclient.spatials.celestial.CelestialObjectSpatial;
import org.quadracoatl.jmeclient.spatials.celestial.SkySpatial;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class CelestialState extends AbstractAppState implements EntityChangeProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(CelestialState.class);
	private Application app = null;
	private Map<String, CelestialObjectSpatial> celestialObjects = new HashMap<>();
	private ClientEnvironment clientEnvironment = null;
	private EntityChangeBatch currentEntityChangeBatch = EntityChangeBatch.EMPTY_BATCH;
	private Node domeNode = new Node();
	private JmeResourceManager resourceManager = null;
	private Map<String, SkySpatial> skies = new HashMap<>();
	
	public CelestialState(ClientEnvironment clientEnvironment) {
		super();
		
		this.clientEnvironment = clientEnvironment;
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		app.getViewPort().detachScene(domeNode);
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.app = app;
		
		resourceManager = (JmeResourceManager)clientEnvironment.getResourceCache();
		
		EntityContainer entityContainer = clientEnvironment.getCurrentRealm().getEntityManager();
		
		for (Entity entity : entityContainer.getEntities(CelestialObjectComponent.class)) {
			createAndAttachCelestialObject(entity, entity.getComponent(CelestialObjectComponent.class));
		}
		
		for (Entity entity : entityContainer.getEntities(SkyComponent.class)) {
			createAndAttachSky(entity, entity.getComponent(SkyComponent.class));
		}
		
		app.getViewPort().attachScene(domeNode);
	}
	
	@Override
	public void process(EntityChangeBatch entityChangeBatch) {
		currentEntityChangeBatch = entityChangeBatch;
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		
		domeNode.updateLogicalState(tpf);
		
		EntityContainer entityContainer = clientEnvironment.getCurrentRealm().getEntityManager();
		
		updateCelestialObjects(entityContainer);
		updateSkies(entityContainer);
		
		domeNode.updateGeometricState();
	}
	
	private final void attach(AbstractSkySpatial spatial) {
		domeNode.attachChildAt(spatial, findInsertIndex(spatial.getOrder()));
	}
	
	private final void createAndAttachCelestialObject(Entity entity, CelestialObjectComponent celestialObjectComponent) {
		LOGGER.info("Attaching new celestial object \"", celestialObjectComponent.getName(), "\" to scene.");
		
		CelestialObjectSpatial celestialObjectSpatial = new CelestialObjectSpatial(celestialObjectComponent, resourceManager, app.getAssetManager());
		
		attach(celestialObjectSpatial);
		celestialObjects.put(celestialObjectComponent.getName(), celestialObjectSpatial);
		
		updateColor(entity, celestialObjectSpatial);
		updateRotation(entity, celestialObjectSpatial);
		updateTexture(entity, celestialObjectSpatial);
		updateSize(entity, celestialObjectSpatial);
	}
	
	private final void createAndAttachSky(Entity entity, SkyComponent skyComponent) {
		LOGGER.info("Attaching new sky \"", skyComponent.getName(), "\" to scene.");
		
		SkySpatial skySpatial = new SkySpatial(skyComponent, resourceManager, app.getAssetManager());
		
		attach(skySpatial);
		skies.put(skySpatial.getName(), skySpatial);
		
		updateColor(entity, skySpatial);
		updateRotation(entity, skySpatial);
		updateTexture(entity, skySpatial);
	}
	
	private final int findInsertIndex(float order) {
		for (int index = 0; index < domeNode.getQuantity(); index++) {
			if (order > ((AbstractSkySpatial)domeNode.getChild(index)).getOrder()) {
				return index;
			}
		}
		
		return domeNode.getQuantity();
	}
	
	private final void updateCelestialObjects(EntityContainer entityContainer) {
		if (currentEntityChangeBatch == null || currentEntityChangeBatch.isEmpty()) {
			return;
		}
		
		for (EntityChange entityChange : currentEntityChangeBatch) {
			// TODO Removal is not handled...like, at all.
			
			Entity entity = entityContainer.getEntity(entityChange.getId());
			
			if (entity != null) {
				CelestialObjectComponent celestialObjectComponent = entity.getComponent(CelestialObjectComponent.class);
				
				if (celestialObjectComponent != null) {
					CelestialObjectSpatial celestialObjectSpatial = celestialObjects.get(celestialObjectComponent.getName());
					
					if (celestialObjectSpatial == null) {
						createAndAttachCelestialObject(entity, celestialObjectComponent);
					} else {
						updateEntity(entity, entityChange, celestialObjectSpatial);
					}
				}
			}
		}
	}
	
	private final void updateColor(Entity entity, AbstractSkySpatial spatial) {
		ColorComponent colorComponent = entity.getComponent(ColorComponent.class);
		if (colorComponent != null) {
			spatial.setColor(colorComponent.getColor(), colorComponent.getColorBlendMode());
		} else {
			spatial.setColor(null, null);
		}
	}
	
	private final void updateEntity(Entity entity, EntityChange entityChange, AbstractSkySpatial spatial) {
		for (ComponentChange componentChange : entityChange.getComponentChanges()) {
			Component changedComponent = componentChange.getComponent();
			
			if (changedComponent instanceof ColorComponent) {
				updateColor(entity, spatial);
			} else if (changedComponent instanceof RotationComponent) {
				updateRotation(entity, spatial);
			} else if (changedComponent instanceof TextureComponent) {
				updateTexture(entity, spatial);
			} else if (changedComponent instanceof TextureComponent) {
				updateSize(entity, spatial);
			}
		}
	}
	
	private final void updateRotation(Entity entity, Spatial spatial) {
		RotationComponent rotationComponent = entity.getComponent(RotationComponent.class);
		if (rotationComponent != null) {
			Vector3d rotation = rotationComponent.getRotation();
			spatial.getLocalRotation().fromAngles(
					(float)MathUtil.toRadians(rotation.x),
					(float)MathUtil.toRadians(rotation.y),
					(float)MathUtil.toRadians(rotation.z));
			spatial.forceRefresh(true, false, false);
		}
	}
	
	private final void updateSize(Entity entity, AbstractSkySpatial spatial) {
		SizeComponent sizeComponent = entity.getComponent(SizeComponent.class);
		if (sizeComponent != null) {
			spatial.setSize(sizeComponent.getSize());
			spatial.forceRefresh(false, true, false);
		}
	}
	
	private final void updateSkies(EntityContainer entityContainer) {
		if (currentEntityChangeBatch == null || currentEntityChangeBatch.isEmpty()) {
			return;
		}
		
		for (EntityChange entityChange : currentEntityChangeBatch) {
			// TODO Removal is not handled...like, at all.
			
			Entity entity = entityContainer.getEntity(entityChange.getId());
			
			if (entity != null) {
				SkyComponent skyComponent = entity.getComponent(SkyComponent.class);
				
				if (skyComponent != null) {
					SkySpatial skySpatial = skies.get(skyComponent.getName());
					
					if (skySpatial == null) {
						createAndAttachSky(entity, skyComponent);
					} else {
						updateEntity(entity, entityChange, skySpatial);
					}
				}
			}
		}
	}
	
	private final void updateTexture(Entity entity, AbstractSkySpatial spatial) {
		TextureComponent textureComponent = entity.getComponent(TextureComponent.class);
		
		if (textureComponent != null) {
			spatial.setTexture(textureComponent.getTexture());
		} else {
			spatial.setTexture(null);
		}
	}
}
