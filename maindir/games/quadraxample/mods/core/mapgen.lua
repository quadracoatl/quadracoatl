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


local air = engine.cosmos.getBlockId("air")
local stone = engine.cosmos.getBlockId("stone")
local dirt = engine.cosmos.getBlockId("dirt")
local grass = engine.cosmos.getBlockId("grass")

local noise = engine.support.createNoise({
	seed = engine.cosmos.seed("primary"),
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

mods.core.realms.default:onChunkCreation(function(realm, chunk, startX, startY, startZ, endX, endY, endZ)
	for indexX = 0, realm:getChunkWidth() - 1, 1 do
		local x = startX + indexX * realm:getBlockWidth()
		
		for indexZ = 0, realm:getChunkDepth() - 1, 1 do
			local z = startZ + indexZ * realm:getBlockDepth()
			
			local maxHeight = noise:get(
				x / realm:getBlockWidth(),
				z / realm:getBlockDepth()) * realm:getBlockHeight()
			local dirtHeight = maxHeight - 3 * realm:getBlockHeight()
			
			for indexY = 0, realm:getChunkHeight() - 1, 1 do
				local y = startY + indexY * realm:getBlockHeight();
				
				if y <= dirtHeight then
					chunk:set(indexX, indexY, indexZ, stone)
				elseif y < maxHeight then
					chunk:set(indexX, indexY, indexZ, dirt)
				elseif y <= maxHeight then
					chunk:set(indexX, indexY, indexZ, grass)
				else
					chunk:set(indexX, indexY, indexZ, air)
				end
			end
		end
	end
end)

