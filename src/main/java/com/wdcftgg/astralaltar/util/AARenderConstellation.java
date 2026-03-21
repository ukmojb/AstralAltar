package com.wdcftgg.astralaltar.util;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

public class AARenderConstellation {

    private static final double EPSILON = 1.0E-6D;
    private static final int PRISM_SIDES_CORE = 12;
    private static final int PRISM_SIDES_GLOW = 10;
    private static final double STAR_SPIN_SPEED = 0.008D; // radians per tick
    private static final float LINE_ALPHA_BOOST = 1.12F;
    private static final float STAR_ALPHA_BOOST = 1.15F;
    private static final float STAR_GLOW_ALPHA_BOOST = 1.20F;
    private static final ResourceLocation FALLBACK_STAR_TEX = new ResourceLocation("astralsorcery", "textures/environment/star1.png");

    private AARenderConstellation() {}

    public static void renderConstellationIntoWorld3D(IConstellation c, Color rC, Vector3 offsetPos, double lineBreadth, RenderConstellation.BrightnessFunction func) {
        renderConstellationIntoWorld3D(c, rC, offsetPos, lineBreadth, 1.0D, func);
    }

    public static void renderConstellationIntoWorld3D(IConstellation c,
                                                       Color rC,
                                                       Vector3 offsetPos,
                                                       double lineBreadth,
                                                       double sizeScale,
                                                       RenderConstellation.BrightnessFunction func) {
        renderConstellationIntoWorld3D(c, rC, offsetPos, lineBreadth, sizeScale, 1.0D, 1.0D, 1.0D, func);
    }

    public static void renderConstellationIntoWorld3D(IConstellation c,
                                                       Color rC,
                                                       Vector3 offsetPos,
                                                       double lineBreadth,
                                                       double sizeScale,
                                                       double lineSizeScale,
                                                       double starSizeScale,
                                                       double depthScale,
                                                       RenderConstellation.BrightnessFunction func) {
        double clampedScale = Math.max(0.01D, sizeScale);
        double clampedLineScale = Math.max(0.01D, lineSizeScale);
        double clampedStarScale = Math.max(0.01D, starSizeScale);
        double clampedDepthScale = Math.max(0.01D, depthScale);
        // Keep the old caller-friendly breadth values, but map them to sensible world-space sizes.
        double visualLineRadius = Math.max(0.02D, lineBreadth * 0.08D * clampedScale * clampedLineScale);
        double visualStarSize = Math.max(0.04D, lineBreadth * 0.22D * clampedScale * clampedStarScale);
        double visualPseudoDepth = Math.max(0.01D, lineBreadth * 0.06D * clampedScale * clampedDepthScale);
        renderConstellationIntoWorld3D(
                c,
                rC,
                offsetPos,
                visualLineRadius,
                clampedScale,
                visualStarSize,
                visualPseudoDepth,
                func
        );
    }

    public static void renderConstellationIntoWorld3D(IConstellation c,
                                                       Color rC,
                                                       Vector3 offsetPos,
                                                       double lineBreadth,
                                                       double sizeScale,
                                                       double starSize,
                                                       double pseudoDepth,
                                                       RenderConstellation.BrightnessFunction func) {
        if (c == null || offsetPos == null || func == null) {
            return;
        }

        float brightness = Math.max(0F, func.getBrightness());
        if (brightness <= 0F) {
            return;
        }

        Color color = rC != null ? rC : Color.WHITE;
        Vec3d origin = toVec3d(offsetPos);
        float rf = color.getRed() / 255.0F;
        float gf = color.getGreen() / 255.0F;
        float bf = color.getBlue() / 255.0F;

        GL11.glPushMatrix();
        try {
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.depthMask(false);

            Collection<?> stars = asCollection(invokeNoArgs(c, "getStars"));
            Vec3d centerLocal = calculateLocalCenter(stars);
            Map<Object, Vec3d> worldStars = new IdentityHashMap<>();
            for (Object star : stars) {
                worldStars.put(star, toWorldPositionCentered(origin, star, centerLocal, sizeScale));
            }

            double minStarDistance = estimateMinDistance(worldStars.values());
            double cappedLineBreadth = lineBreadth;
            double cappedStarSize = starSize;
            double cappedPseudoDepth = pseudoDepth;
            if (minStarDistance > 0D) {
                cappedLineBreadth = Math.min(cappedLineBreadth, minStarDistance * 0.22D);
                cappedStarSize = Math.min(cappedStarSize, minStarDistance * 0.38D);
                cappedPseudoDepth = Math.min(cappedPseudoDepth, minStarDistance * 0.16D);
            }

            for (Object connection : asCollection(invokeNoArgs(c, "getStarConnections"))) {
                Object starA = resolveStarEndpoint(connection, "A");
                Object starB = resolveStarEndpoint(connection, "B");
                if (starA == null || starB == null) {
                    continue;
                }

                Vec3d worldA = worldStars.containsKey(starA)
                        ? worldStars.get(starA)
                        : toWorldPositionCentered(origin, starA, centerLocal, sizeScale);
                Vec3d worldB = worldStars.containsKey(starB)
                        ? worldStars.get(starB)
                        : toWorldPositionCentered(origin, starB, centerLocal, sizeScale);

                int lineSeed = System.identityHashCode(connection);
                float lineAlphaPulse = 0.72F + 0.28F * temporalWave(lineSeed, 0.17D, true);
                double lineWidthPulse = 0.90D + 0.22D * temporalWave(lineSeed * 31 + 7, 0.11D, false);
                double lineDepthPulse = 0.88D + 0.24D * temporalWave(lineSeed * 17 + 3, 0.09D, false);

                renderPseudo3DConnection(
                        worldA,
                        worldB,
                        cappedLineBreadth * lineWidthPulse,
                        cappedPseudoDepth * lineDepthPulse,
                        rf,
                        gf,
                        bf,
                        clamp01f(brightness * lineAlphaPulse * LINE_ALPHA_BOOST)
                );
            }

            // Deterministic ordering avoids alpha-blended sparkle flicker between frames.
            List<Map.Entry<Object, Vec3d>> orderedStars = new ArrayList<>(worldStars.entrySet());
            orderedStars.sort(Comparator
                    .comparingDouble((Map.Entry<Object, Vec3d> e) -> e.getValue().x)
                    .thenComparingDouble(e -> e.getValue().z));

            Minecraft.getMinecraft().renderEngine.bindTexture(FALLBACK_STAR_TEX);
            for (Map.Entry<Object, Vec3d> starEntry : orderedStars) {
                Vec3d center = starEntry.getValue();
                int starSeed = System.identityHashCode(starEntry.getKey());

                float starAlphaPulse = 0.70F + 0.30F * temporalWave(starSeed, 0.21D, true);
                double starSizePulse = 0.86D + 0.28D * temporalWave(starSeed * 13 + 11, 0.14D, false);
                double starDepthPulse = 0.85D + 0.20D * temporalWave(starSeed * 23 + 5, 0.10D, false);

                renderStarCrystalFlat3D(
                        center,
                        cappedStarSize * starSizePulse,
                        cappedPseudoDepth * starDepthPulse,
                        rf,
                        gf,
                        bf,
                        clamp01f(brightness * starAlphaPulse * STAR_ALPHA_BOOST)
                );
            }

            // Weak additive highlight to give stars a soft magical bloom.
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            for (Map.Entry<Object, Vec3d> starEntry : orderedStars) {
                Vec3d center = starEntry.getValue();
                int starSeed = System.identityHashCode(starEntry.getKey());
                float glowPulse = 0.62F + 0.38F * temporalWave(starSeed * 43 + 19, 0.13D, false);
                double glowSizePulse = 0.92D + 0.18D * temporalWave(starSeed * 29 + 2, 0.08D, false);
                renderStarAdditiveGlow(
                        center,
                        cappedStarSize * glowSizePulse,
                        cappedPseudoDepth,
                        rf,
                        gf,
                        bf,
                        clamp01f(brightness * 0.95F * glowPulse * STAR_GLOW_ALPHA_BOOST)
                );
            }
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        } finally {
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GL11.glPopMatrix();
            TextureHelper.refreshTextureBindState();
        }
    }

    private static void renderPseudo3DConnection(Vec3d start,
                                                  Vec3d end,
                                                  double radius,
                                                  double pseudoDepth,
                                                  float r,
                                                  float g,
                                                  float b,
                                                  float a) {
        // Render one true 3D tube segment (polygonal prism), not billboard strips.
        renderConnectionPrism(start, end, Math.max(0.001D, radius), PRISM_SIDES_CORE, r, g, b, a);
        if (pseudoDepth > 0D) {
            renderConnectionPrism(start, end, Math.max(0.001D, radius + pseudoDepth * 0.35D), PRISM_SIDES_GLOW, r, g, b, a * 0.32F);
        }
    }

    private static void renderConnectionPrism(Vec3d start,
                                              Vec3d end,
                                              double radius,
                                              int sides,
                                              float r,
                                              float g,
                                              float b,
                                              float a) {
        Vec3d axis = end.subtract(start);
        if (axis.lengthSquared() < EPSILON || sides < 3) {
            return;
        }
        axis = axis.normalize();

        Vec3d ref = Math.abs(axis.y) < 0.99D ? new Vec3d(0, 1, 0) : new Vec3d(1, 0, 0);
        Vec3d basisU = axis.crossProduct(ref);
        if (basisU.lengthSquared() < EPSILON) {
            basisU = axis.crossProduct(new Vec3d(0, 0, 1));
        }
        basisU = basisU.normalize();
        Vec3d basisV = axis.crossProduct(basisU).normalize();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bld = tess.getBuffer();
        GlStateManager.disableTexture2D();
        bld.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i < sides; i++) {
            double a0 = (Math.PI * 2D * i) / sides;
            double a1 = (Math.PI * 2D * (i + 1)) / sides;

            Vec3d off0 = basisU.scale(Math.cos(a0) * radius).add(basisV.scale(Math.sin(a0) * radius));
            Vec3d off1 = basisU.scale(Math.cos(a1) * radius).add(basisV.scale(Math.sin(a1) * radius));

            Vec3d s0 = start.add(off0);
            Vec3d s1 = start.add(off1);
            Vec3d e1 = end.add(off1);
            Vec3d e0 = end.add(off0);

            bld.pos(s0.x, s0.y, s0.z).color(r, g, b, a).endVertex();
            bld.pos(s1.x, s1.y, s1.z).color(r, g, b, a).endVertex();
            bld.pos(e1.x, e1.y, e1.z).color(r, g, b, a).endVertex();
            bld.pos(e0.x, e0.y, e0.z).color(r, g, b, a).endVertex();
        }

        tess.draw();
        GlStateManager.enableTexture2D();
    }

    private static void renderStarCrystalFlat3D(Vec3d center,
                                                 double size,
                                                 double pseudoDepth,
                                                 float r,
                                                 float g,
                                                 float b,
                                                 float a) {
        double spin = getStarSpinAngle();
        Vec3d toCam = getPlayerViewToCamera(center).normalize();
        Vec3d right = toCam.crossProduct(new Vec3d(0, 1, 0));
        if (right.lengthSquared() < EPSILON) {
            right = new Vec3d(1, 0, 0);
        }
        right = right.normalize();
        Vec3d up = right.crossProduct(toCam);
        if (up.lengthSquared() < EPSILON) {
            up = new Vec3d(0, 1, 0);
        }
        up = up.normalize();

        Vec3d front = toCam.scale(Math.max(0.001D, pseudoDepth));
        renderBillboardStarLayer(center.add(front), right, up, size, size * 0.75D, spin, r, g, b, a);
        renderBillboardStarLayer(center, right, up, size * 0.95D, size * 0.7D, spin + Math.PI / 3.0D, r, g, b, a * 0.92F);
        renderBillboardStarLayer(center.subtract(front), right, up, size * 0.85D, size * 0.62D, spin - Math.PI / 3.0D, r, g, b, a * 0.82F);
    }

    private static void drawStrip(Vec3d start,
                                  Vec3d end,
                                  Vec3d offset,
                                  float r,
                                  float g,
                                  float b,
                                  float a) {
        Vec3d p1 = new Vec3d(start.x - offset.x, start.y, start.z - offset.z);
        Vec3d p2 = new Vec3d(start.x + offset.x, start.y, start.z + offset.z);
        Vec3d p3 = new Vec3d(end.x + offset.x, end.y, end.z + offset.z);
        Vec3d p4 = new Vec3d(end.x - offset.x, end.y, end.z - offset.z);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bld = tess.getBuffer();
        bld.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        drawQuad(bld, p1, p2, p3, p4, r, g, b, a);
        tess.draw();
    }

    private static void renderBillboardStarLayer(Vec3d center,
                                                 Vec3d right,
                                                 Vec3d up,
                                                 double halfSize,
                                                 double halfDepth,
                                                 double rot,
                                                 float r,
                                                 float g,
                                                 float b,
                                                 float a) {
        Vec3d axisA = right.scale(Math.cos(rot)).add(up.scale(Math.sin(rot))).normalize().scale(halfSize);
        Vec3d axisB = up.scale(Math.cos(rot)).subtract(right.scale(Math.sin(rot))).normalize().scale(halfDepth);

        Vec3d p1 = center.subtract(axisA).subtract(axisB);
        Vec3d p2 = center.add(axisA).subtract(axisB);
        Vec3d p3 = center.add(axisA).add(axisB);
        Vec3d p4 = center.subtract(axisA).add(axisB);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bld = tess.getBuffer();
        bld.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        drawQuad(bld, p1, p2, p3, p4, r, g, b, a);
        tess.draw();
    }

    private static Vec3d getToCamera(Vec3d from) {
        Entity cam = Minecraft.getMinecraft().getRenderViewEntity();
        if (cam == null) {
            return new Vec3d(0, 0, 1);
        }
        float pt = Minecraft.getMinecraft().getRenderPartialTicks();
        Vec3d camPos = new Vec3d(
                cam.lastTickPosX + (cam.posX - cam.lastTickPosX) * pt,
                cam.lastTickPosY + (cam.posY - cam.lastTickPosY) * pt,
                cam.lastTickPosZ + (cam.posZ - cam.lastTickPosZ) * pt
        );
        Vec3d v = camPos.subtract(from);
        if (v.lengthSquared() < EPSILON) {
            return new Vec3d(0, 0, 1);
        }
        return v.normalize();
    }

    private static Vec3d getPlayerViewToCamera(Vec3d from) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.player != null) {
            Vec3d look = mc.player.getLook(mc.getRenderPartialTicks());
            if (look != null && look.lengthSquared() >= EPSILON) {
                // Use player view orientation so star billboards always rotate with the player's camera.
                return look.normalize().scale(-1D);
            }
        }
        return getToCamera(from);
    }

    private static double estimateMinDistance(Collection<Vec3d> points) {
        if (points == null || points.size() < 2) {
            return 0D;
        }
        double minSq = Double.MAX_VALUE;
        Vec3d[] arr = points.toArray(new Vec3d[0]);
        for (int i = 0; i < arr.length; i++) {
            Vec3d a = arr[i];
            for (int j = i + 1; j < arr.length; j++) {
                Vec3d b = arr[j];
                double dx = a.x - b.x;
                double dy = a.y - b.y;
                double dz = a.z - b.z;
                double d2 = dx * dx + dy * dy + dz * dz;
                if (d2 < minSq) {
                    minSq = d2;
                }
            }
        }
        if (minSq == Double.MAX_VALUE) {
            return 0D;
        }
        return Math.sqrt(minSq);
    }

    private static void drawQuad(BufferBuilder bld,
                                 Vec3d p1,
                                 Vec3d p2,
                                 Vec3d p3,
                                 Vec3d p4,
                                 float r,
                                 float g,
                                 float b,
                                 float a) {
        bld.pos(p1.x, p1.y, p1.z).tex(0, 1).color(r, g, b, a).endVertex();
        bld.pos(p2.x, p2.y, p2.z).tex(1, 1).color(r, g, b, a).endVertex();
        bld.pos(p3.x, p3.y, p3.z).tex(1, 0).color(r, g, b, a).endVertex();
        bld.pos(p4.x, p4.y, p4.z).tex(0, 0).color(r, g, b, a).endVertex();
    }

    private static Vec3d toWorldPositionCentered(Vec3d centerPos,
                                                 Object starPoint,
                                                 Vec3d localCenter,
                                                 double sizeScale) {
        Vec3d local = readLocalPoint(starPoint);
        return new Vec3d(
                centerPos.x + (local.x - localCenter.x) * sizeScale,
                centerPos.y,
                centerPos.z + (local.z - localCenter.z) * sizeScale
        );
    }

    private static Vec3d calculateLocalCenter(Collection<?> stars) {
        if (stars == null || stars.isEmpty()) {
            return new Vec3d(0, 0, 0);
        }
        double sx = 0;
        double sz = 0;
        int count = 0;
        for (Object star : stars) {
            Vec3d local = readLocalPoint(star);
            sx += local.x;
            sz += local.z;
            count++;
        }
        if (count <= 0) {
            return new Vec3d(0, 0, 0);
        }
        return new Vec3d(sx / count, 0, sz / count);
    }

    private static Vec3d readLocalPoint(Object starPoint) {
        double x = readNumber(starPoint, "getPosX", "getX", "x", "posX");
        // Astral constellation points are usually 2D (X/Y). We map that Y onto world Z.
        double z = readNumber(starPoint, "getPosY", "getY", "y", "posY", "getPosZ", "getZ", "z", "posZ");
        return new Vec3d(x, 0, z);
    }

    private static Vec3d toVec3d(Vector3 in) {
        double x = readNumber(in, "getX", "x");
        double y = readNumber(in, "getY", "y");
        double z = readNumber(in, "getZ", "z");
        return new Vec3d(x, y, z);
    }

    private static Object resolveStarEndpoint(Object connection, String suffix) {
        Object byMethod = invokeNoArgs(connection, "getStar" + suffix);
        if (byMethod != null) {
            return byMethod;
        }
        Object byPosMethod = invokeNoArgs(connection, "getPos" + suffix);
        if (byPosMethod != null) {
            return byPosMethod;
        }
        Object byField = readObject(connection, "star" + suffix, "point" + suffix, "pos" + suffix);
        if (byField != null) {
            return byField;
        }

        // Fallback names seen in different mappings/versions.
        if ("A".equals(suffix)) {
            Object alt = readObject(connection,
                    "getStart", "getFrom", "getFirst", "getOne",
                    "start", "from", "first", "one", "a");
            if (alt != null) {
                return alt;
            }
        } else {
            Object alt = readObject(connection,
                    "getEnd", "getTo", "getSecond", "getTwo",
                    "end", "to", "second", "two", "b");
            if (alt != null) {
                return alt;
            }
        }
        return null;
    }

    private static Collection<?> asCollection(Object value) {
        if (value instanceof Collection) {
            return (Collection<?>) value;
        }
        return java.util.Collections.emptyList();
    }

    private static Object invokeNoArgs(Object instance, String methodName) {
        if (instance == null) {
            return null;
        }
        try {
            Method method = instance.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(instance);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static double readNumber(Object instance, String... names) {
        Object value = readObject(instance, names);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0D;
    }

    private static Object readObject(Object instance, String... names) {
        if (instance == null) {
            return null;
        }
        for (String name : names) {
            Object byMethod = invokeNoArgs(instance, name);
            if (byMethod != null) {
                return byMethod;
            }
            try {
                Field field = instance.getClass().getDeclaredField(name);
                field.setAccessible(true);
                Object value = field.get(instance);
                if (value != null) {
                    return value;
                }
            } catch (Exception ignored) {
            }
            try {
                Field field = instance.getClass().getField(name);
                field.setAccessible(true);
                Object value = field.get(instance);
                if (value != null) {
                    return value;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }


    private static void renderStarAdditiveGlow(Vec3d center,
                                               double size,
                                               double pseudoDepth,
                                               float r,
                                               float g,
                                               float b,
                                               float a) {
        double spin = getStarSpinAngle() * 0.85D;
        Vec3d toCam = getPlayerViewToCamera(center).normalize();
        Vec3d right = toCam.crossProduct(new Vec3d(0, 1, 0));
        if (right.lengthSquared() < EPSILON) {
            right = new Vec3d(1, 0, 0);
        }
        right = right.normalize();
        Vec3d up = right.crossProduct(toCam);
        if (up.lengthSquared() < EPSILON) {
            up = new Vec3d(0, 1, 0);
        }
        up = up.normalize();

        float glowAlpha = Math.max(0.006F, a * 0.1F);
        double frontDepth = Math.max(0.0005D, pseudoDepth * 0.35D);
        Vec3d front = toCam.scale(frontDepth);

        renderBillboardStarLayer(center, right, up, size * 1.03D, size * 0.72D, spin, r, g, b, glowAlpha);
        renderBillboardStarLayer(center.add(front), right, up, size * 0.82D, size * 0.56D, spin + Math.PI / 4.0D, r, g, b, glowAlpha * 0.6F);
    }

    private static double getStarSpinAngle() {
        float partial = Minecraft.getMinecraft().getRenderPartialTicks();
        return (ClientScheduler.getClientTick() + partial) * STAR_SPIN_SPEED;
    }

    private static float temporalWave(int seed, double speed, boolean useConstellationCurve) {
        long t = getRenderTime();
        float partial = Minecraft.getMinecraft().getRenderPartialTicks();
        double phase = (fract(seed * 0.61803398875D) * Math.PI * 2D);

        float localSin = (float) (0.5D + 0.5D * Math.sin((t + partial) * speed + phase));
        if (!useConstellationCurve) {
            return localSin;
        }

        int divisor = 6 + Math.abs(seed % 5);
        float conLike = RenderConstellation.conSFlicker(t, partial + (float) (phase * 0.05D), divisor);
        return clamp01f(localSin * 0.55F + conLike * 0.45F);
    }

    private static long getRenderTime() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null) {
            return mc.world.getTotalWorldTime();
        }
        return ClientScheduler.getClientTick();
    }

    private static double fract(double v) {
        return v - Math.floor(v);
    }

    private static float clamp01f(float v) {
        return Math.max(0F, Math.min(1F, v));
    }
}
