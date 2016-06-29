

engine.cosmos.registerBlock({
	name = "stone",
	texture = "blocks/stone.png"
});

engine.cosmos.registerBlock({
	name = "dirt",
	texture = "blocks/dirt.png"
});

engine.cosmos.registerBlock({
	name = "grass",
	texture = {
		"blocks/grass-top.png",
		"blocks/dirt.png",
		"blocks/dirt-grass.png",
		"blocks/dirt-grass.png",
		"blocks/dirt-grass.png",
		"blocks/dirt-grass.png"
	}
});



engine.cosmos.registerRealm({
	name = "default",
	chunkWidth = 64,
	chunkHeight = 64,
	chunkDepth = 64,
	blockWidth = 1,
	blockHeight = 1,
	blockDepth = 1
})

engine.cosmos.addSky("default", {
	name = "sky",
	texture = {
		"sky/skybox-top.png",
		"sky/skybox-bottom.png",
		"sky/skybox-side-clouds.png",
		"sky/skybox-side-clouds.png",
		"sky/skybox-side-clouds.png",
		"sky/skybox-side-clouds.png"
	}
})



local stone = engine.cosmos.getBlockId("stone")
local dirt = engine.cosmos.getBlockId("dirt")
local grass = engine.cosmos.getBlockId("grass")

local noise = engine.support.createNoise({
	seed = "primary",
	octaves = 7,
	persistence = 0.7,
	scaleX = 128,
	scaleY = 128,
	scaleZ = 1,
	scaleW = 1,
	transform = function(value, x, y, z, w)
		return math.floor(value * 64)
	end
})

engine.cosmos.onChunkCreation("default", function(realm, startX, startY, startZ, endX, endY, endZ)
	for x = startX, endX, realm:getBlockWidth() do
		for z = startZ, endZ, realm:getBlockDepth() do
			local maxHeight = noise:get(x, z)
			
			for y = startY, endY, realm:getBlockHeight() do
				if y <= (maxHeight - 3) then
					realm:setBlock(x, y, z, stone)
				elseif y < maxHeight then
					realm:setBlock(x, y, z, dirt)
				elseif y == maxHeight then
					realm:setBlock(x, y, z, grass)
				end
			end
		end
	end
end)

