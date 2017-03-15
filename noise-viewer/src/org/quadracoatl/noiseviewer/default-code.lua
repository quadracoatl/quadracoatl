-- You can edit this code as you wish,
-- you can always add more noises or transform
-- the returned value in any way.

local noise = engine.support.createNoise({
	seed = "viewer",
	type = NoiseType.OPEN_SIMPLEX,
	octaves = 7,
	persistence = 0.7,
	scaleX = 128,
	scaleY = 128
})

local random = engine.support.createRandom({
	seed = "viewer",
	transform = function(value)
		return value * 0.1
	end
})

-- The function is returned because it is
-- used to fill the area to the right.
return function(x, y)
	return noise:get(x, y) + random:next()
end
