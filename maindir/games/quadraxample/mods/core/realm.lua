--[[
Copyright (c) 2017, Robert 'Bobby' Zenz
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--]]


mods.core.realms = {}

mods.core.realms.default = engine.cosmos.registerRealm({
	name = "default",
	chunkWidth = 16,
	chunkHeight = 32,
	chunkDepth = 16,
	blockWidth = 1.0,
	blockHeight = 1.0,
	blockDepth = 1.0,
	onTimeUpdate = function(currentTime, elapsedNanoSecondsSinceLastUpdate)
		local elapsedSeconds = elapsedNanoSecondsSinceLastUpdate / 1000 / 1000 / 1000;
		local difference = elapsedSeconds / 60
		local newTime = (currentTime + difference) % 1.0
		
		mods.core.realms.default.currentLightValue = 2 * math.pi * newTime
		
		return newTime
	end
})

local defaultRealm = mods.core.realms.default;

mods.core.realms.default:addSky({
	name = "sky",
	color = 0xffcccc00,
	colorBlendMode = ColorBlendMode.SUBTRACT,
	order = 100.0,
	texture = {
		"sky/skybox-side-clouds.png",
		"sky/skybox-side-clouds.png",
		"sky/skybox-side-clouds.png",
		"sky/skybox-side-clouds.png",
		"sky/skybox-top.png",
		"sky/skybox-bottom.png"
	},
	onUpdate = function(sky, currentRealmTime, elapsedNanoSecondsSinceLastUpdate)
		local color = sky:getColor()
		color.alpha = math.max(0.0, math.min(1.0, math.sin(-defaultRealm.currentLightValue) * 1.33 + 0.5))
		sky:setColor(color)
	end
})

mods.core.realms.default:addLightSource({
	light = {
		color = 0x415155ff,
		type = LightSourceType.AMBIENT
	},
	onUpdate = function(lightSource, currentRealmTime, elapsedNanoSecondsSinceLastUpdate)
		local light = lightSource:getLight()
		light.color.alpha = math.abs(math.max(-0.1, math.min(1.0, math.sin(defaultRealm.currentLightValue) * 1.33 + 0.5)))
		lightSource:setLight(light)
	end
})

mods.core.realms.default:addCelestialObject({
	name = "sun",
	light = {
		color = 0xffeeeeff,
		type = LightSourceType.DIRECTIONAL
	},
	order = 75.0,
	size = 0.9,
	texture = "sky/sun.png",
	onUpdate = function(sun, currentRealmTime, elapsedNanoSecondsSinceLastUpdate)
		local rotation = sun:getRotation()
		rotation.z = currentRealmTime
		sun:setRotation(rotation)
		
		local light = sun:getLight()
		light.direction.x = -math.cos(defaultRealm.currentLightValue)
		light.direction.y = -math.sin(defaultRealm.currentLightValue)
		light.color.alpha = math.max(0.0, math.min(1.0, math.sin(defaultRealm.currentLightValue) * 1.33 + 0.5))
		sun:setLight(light)
	end
})

mods.core.realms.default:addCelestialObject({
	name = "moon",
	light = {
		color = 0x22223300,
		type = LightSourceType.DIRECTIONAL
	},
	order = 50.0,
	size = 0.7,
	texture = "sky/moon.png",
	onUpdate = function(moon, currentRealmTime, elapsedNanoSecondsSinceLastUpdate)
		local rotation = moon:getRotation()
		rotation.z = (currentRealmTime + 0.5) % 1.0
		moon:setRotation(rotation)
		
		local light = moon:getLight()
		light.direction.x = math.cos(2 * math.pi * currentRealmTime)
		light.direction.y = math.sin(2 * math.pi * currentRealmTime)
		light.color.alpha = math.max(0.0, math.min(1.0, math.sin(-defaultRealm.currentLightValue) * 1.33 + 0.5))
		moon:setLight(light)
	end
})

