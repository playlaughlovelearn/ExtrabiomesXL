/**
 * This work is licensed under the Creative Commons
 * Attribution-ShareAlike 3.0 Unported License. To view a copy of this
 * license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package extrabiomes.module.summa.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import extrabiomes.lib.Element;
import extrabiomes.module.summa.TreeSoilRegistry;

public class WorldGenRedwood extends WorldGenerator
{
    
    private enum TreeBlock
    {
        LEAVES(new ItemStack(Block.leaves)), TRUNK(new ItemStack(Block.wood, 1, 1));
        
        private ItemStack      stack;
        
        private static boolean loadedCustomBlocks = false;
        
        private static void loadCustomBlocks()
        {
            if (Element.LEAVES_REDWOOD.isPresent())
            {
                LEAVES.stack = Element.LEAVES_REDWOOD.get();
            }
            
            if(Element.LOG_QUARTER_REDWOOD.isPresent()){
            	TRUNK.stack = Element.LOG_QUARTER_REDWOOD.get();
            }
            
            loadedCustomBlocks = true;
        }
        
        TreeBlock(ItemStack stack)
        {
            this.stack = stack;
        }
        
        public int getID()
        {
            if (!loadedCustomBlocks)
                loadCustomBlocks();
            return stack.itemID;
        }
        
        public int getMetadata()
        {
            if (!loadedCustomBlocks)
                loadCustomBlocks();
            return stack.getItemDamage();
        }
        
    }
    
    public WorldGenRedwood(boolean doNotify)
    {
        super(doNotify);
    }
    
    // Store the last seed that was used to generate a tree
    private static long lastSeed = 0;
    
    @Override
    public boolean generate(World world, Random rand, int x, int y, int z)
    {
        // Store the seed
        lastSeed = rand.nextLong();
        
        return generateTree(world, new Random(lastSeed), x, y, z);
    }
    
    public boolean generate(World world, long seed, int x, int y, int z)
    {
        // Store the seed
        lastSeed = seed;
        
        return generateTree(world, new Random(seed), x, y, z);
    }
    
    private boolean generateTree(World world, Random rand, int x, int y, int z)
    {
        final int height = rand.nextInt(30) + 32;
        final int j = 1 + rand.nextInt(12);
        final int k = height - j;
        final int l = 2 + rand.nextInt(6);
        
        if (y < 1 || y + height + 1 > 256)
            return false;
        
        if (!TreeSoilRegistry.isValidSoil(world.getBlockId(x, y - 1, z)) || y >= 256 - height - 1)
            return false;
        
        for (int y1 = y; y1 <= y + 1 + height; y1++)
        {
            int k1 = 1;
            
            if (y1 - y < j)
            {
                k1 = 0;
            }
            else
            {
                k1 = l;
            }
            
            for (int x1 = x - k1; x1 <= x + k1; x1++)
            {
                for (int z1 = z - k1; z1 <= z + k1; z1++)
                {
                    if (y1 >= 0 && y1 < 256)
                    {
                        final int id = world.getBlockId(x1, y1, z1);
                        
                        if (Block.blocksList[id] != null
                                && Block.blocksList[id].isLeaves(world, x1, y1,
                                        z1))
                            return false;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
        
        world.setBlock(x, y - 1, z, Block.dirt.blockID);
        world.setBlock(x - 1, y - 1, z, Block.dirt.blockID);
        world.setBlock(x, y - 1, z - 1, Block.dirt.blockID);
        world.setBlock(x - 1, y - 1, z - 1, Block.dirt.blockID);
        int l1 = rand.nextInt(2);
        int j2 = 1;
        boolean flag1 = false;
        
        for (int i3 = 0; i3 <= k; i3++)
        {
            final int y1 = y + height - i3;
            
            for (int x1 = x - l1; x1 <= x + l1; x1++)
            {
                final int k4 = x1 - x;
                
                for (int z1 = z - l1; z1 <= z + l1; z1++)
                {
                    final int i5 = z1 - z;
                    
                    if (Math.abs(k4) != l1 || Math.abs(i5) != l1 || l1 <= 0)
                    {
                        int blockID = world.getBlockId(x1, y1, z1);
                        if (blockID == 0 || Block.blocksList[blockID].canBeReplacedByLeaves(world, x1, y1, z1))
                        {
                            setBlockAndMetadata(world, x1, y1, z1, TreeBlock.LEAVES.getID(), TreeBlock.LEAVES.getMetadata());
                        }
                        
                        blockID = world.getBlockId(x1 - 1, y1, z1);
                        if (blockID == 0 || Block.blocksList[blockID].canBeReplacedByLeaves(world, x1 - 1, y1, z1))
                        {
                            setBlockAndMetadata(world, x1 - 1, y1, z1, TreeBlock.LEAVES.getID(), TreeBlock.LEAVES.getMetadata());
                        }
                        
                        blockID = world.getBlockId(x1, y1, z1 - 1);
                        if (blockID == 0 || Block.blocksList[blockID].canBeReplacedByLeaves(world, x1, y1, z1 - 1))
                        {
                            setBlockAndMetadata(world, x1, y1, z1 - 1, TreeBlock.LEAVES.getID(), TreeBlock.LEAVES.getMetadata());
                        }
                        
                        blockID = world.getBlockId(x1 - 1, y1, z1 - 1);
                        if (blockID == 0 || Block.blocksList[blockID].canBeReplacedByLeaves(world, x1 - 1, y1, z1 - 1))
                        {
                            setBlockAndMetadata(world, x1 - 1, y1, z1 - 1, TreeBlock.LEAVES.getID(), TreeBlock.LEAVES.getMetadata());
                        }
                    }
                }
            }
            
            if (l1 >= j2)
            {
                l1 = flag1 ? 1 : 0;
                flag1 = true;
                
                if (++j2 > l)
                {
                    j2 = l;
                }
            }
            else
            {
                l1++;
            }
        }
        
        final int j3 = rand.nextInt(3);
        
        for (int y1 = 0; y1 < height - j3; y1++)
        {
            final int j4 = world.getBlockId(x, y + y1, z);
            
            if (Block.blocksList[j4] == null || Block.blocksList[j4].isLeaves(world, x, y + y1, z))
            {

            	setBlockAndMetadata(world, x, y + y1, z, TreeBlock.TRUNK.getID(), 2);
                setBlockAndMetadata(world, x - 1, y + y1, z, TreeBlock.TRUNK.getID(), 3);
                setBlockAndMetadata(world, x, y + y1, z - 1, TreeBlock.TRUNK.getID(), 1);
                setBlockAndMetadata(world, x - 1, y + y1, z - 1, TreeBlock.TRUNK.getID(), 0);
                
            }
        }
        
        return true;
    }
    
    public static long getLastSeed()
    {
        return lastSeed;
    }
}
