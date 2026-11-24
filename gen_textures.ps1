# Silent Cart texture generator
# - Entity: copy vanilla minecart.png, recolor wheel nubs (rows 10-11, 4 bumps) to slime green
# - Item: copy vanilla item minecart.png, recolor bright highlight pixels (wheels) to slime green
Add-Type -AssemblyName System.Drawing

$srcEntity = ".tmp\minecart.png"
$srcItem   = ".tmp\item_minecart.png"
$dstEntity = "src\main\resources\assets\silent-cart\textures\entity\minecart\silent_minecart.png"
$dstItem   = "src\main\resources\assets\silent-cart\textures\item\silent_minecart.png"

New-Item -ItemType Directory -Force -Path (Split-Path $dstEntity) | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path $dstItem) | Out-Null

# slime base color (slime ball-ish)
$sR = 104; $sG = 200; $sB = 86

function Recolor([System.Drawing.Bitmap]$bmp, $cond, $doubleFactor) {
  for ($y = 0; $y -lt $bmp.Height; $y++) {
    for ($x = 0; $x -lt $bmp.Width; $x++) {
      $c = $bmp.GetPixel($x, $y)
      if ($c.A -eq 0) { continue }
      if (& $cond $x $y $c) {
        $lum = ($c.R * 0.299 + $c.G * 0.587 + $c.B * 0.114) / 255.0
        $f = 0.35 + 0.9 * $lum * $doubleFactor
        $r = [int][Math]::Min(255, $sR * $f)
        $g = [int][Math]::Min(255, $sG * $f)
        $b = [int][Math]::Min(255, $sB * $f)
        $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb($c.A, $r, $g, $b))
      }
    }
  }
}

# --- Entity texture: wheel bumps at rows 10-11, columns 5-8,15-18,25-28,35-38
$bmp = New-Object System.Drawing.Bitmap $srcEntity
$condE = {
  param($x, $y, $c)
  if ($y -lt 10 -or $y -gt 11) { return $false }
  foreach ($o in 5, 15, 25, 35) { if ($x -ge $o -and $x -le ($o + 3)) { return $true } }
  return $false
}
Recolor $bmp $condE 1.0
$bmp.Save($dstEntity, [System.Drawing.Imaging.ImageFormat]::Png)
$big = New-Object System.Drawing.Bitmap ($bmp.Width * 8), ($bmp.Height * 8)
$g = [System.Drawing.Graphics]::FromImage($big)
$g.InterpolationMode = 'NearestNeighbor'; $g.PixelOffsetMode = 'Half'
$g.DrawImage($bmp, 0, 0, $big.Width, $big.Height)
$big.Save(".tmp\silent_minecart_entity_big.png", [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()

# --- Item texture: bright pixels (wheel highlights) in rows 7..12
$bmp2 = New-Object System.Drawing.Bitmap $srcItem
$condI = {
  param($x, $y, $c)
  if ($y -lt 7 -or $y -gt 12) { return $false }
  $lum = ($c.R * 0.299 + $c.G * 0.587 + $c.B * 0.114)
  return ($lum -ge 150)
}
Recolor $bmp2 $condI 1.0
$bmp2.Save($dstItem, [System.Drawing.Imaging.ImageFormat]::Png)
$big2 = New-Object System.Drawing.Bitmap ($bmp2.Width * 10), ($bmp2.Height * 10)
$g2 = [System.Drawing.Graphics]::FromImage($big2)
$g2.InterpolationMode = 'NearestNeighbor'; $g2.PixelOffsetMode = 'Half'
$g2.DrawImage($bmp2, 0, 0, $big2.Width, $big2.Height)
$big2.Save(".tmp\silent_minecart_item_big.png", [System.Drawing.Imaging.ImageFormat]::Png)
$bmp2.Dispose()

Write-Output "OK: $dstEntity / $dstItem"
