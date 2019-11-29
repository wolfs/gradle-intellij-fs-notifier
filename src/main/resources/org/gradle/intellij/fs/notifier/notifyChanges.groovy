import org.gradle.internal.vfs.VirtualFileSystem

println "Hello world"

def vfs = gradle.services.get(VirtualFileSystem)
println "Got VFS!"